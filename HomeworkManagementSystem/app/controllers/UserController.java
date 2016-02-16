package controllers;

import java.util.List;
import java.util.Map.Entry;

import models.Message;
import models.Token;
import models.Token.Type;
import models.User;

import org.joda.time.DateTime;

import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import provider.SimpleUserPassAuthProvider;
import provider.SimpleUserPassAuthProvider.SimpleAuthSignup;
import provider.SimpleUserPassAuthProvider.SimpleLogin;
import security.HDSDynamicResourceHandler;
import util.IUser;
import util.SimpleAuthUtil;
import utils.ErrorHelper;
import utils.ErrorHelper.ErrorHandler;
import utils.UserUtil;
import be.objectify.deadbolt.java.actions.Dynamic;
import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;

import forms.SimpleForms;
import forms.SimpleForms.EmailForm;
import forms.UserForms;
import forms.UserForms.PasswordResetForm;

/**
 * Application Controller for authentication and authorization
 * All available user stuff is placed here
 * 
 * @version 1.0
 */
public class UserController extends Controller
{

   /**
    * Show specific User data by ID
    * 
    * @param idU
    * @return show-user-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result showUser(Long idU)
   {
      return ok(views.html.user.showuser.render(UserForms.LoginForm(), User.findUserByID(idU)));
   }

   /**
    * Create a new user
    * 
    * @return create-user-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result createUser()
   {
      return ok(views.html.user.createuser.render(UserForms.LoginForm()));
   }

   /**
    * Handle login form submission.
    * 
    * @return index-view (badRequest)
    * @return redirect Application.index()
    */
   @SubjectNotPresent
   public static Result login()
   {
      Form<SimpleLogin> loginForm = UserForms.LoginForm().bindFromRequest();
      if (loginForm.hasErrors())
      {
         flashValidationErrors(loginForm);
         return badRequest(views.html.index.render());
      }
      else
      {
         Result result = UsernamePasswordAuthProvider.handleLogin(ctx());
         User loggedInUser = User.findBySession(session());
         if (loggedInUser != null)
         {
            loggedInUser.setLastLogin(DateTime.now());
            loggedInUser.save();
         }
         String originalRequestedUrl = request().getHeader("referer");

         String host = request().host();
         try
         {
            if (originalRequestedUrl != null && originalRequestedUrl.contains(host))
            {
               String subPath = originalRequestedUrl.substring(originalRequestedUrl.indexOf(request().host())
                     + (host.length() + 1));
               if (!subPath.isEmpty())
               {
                  return redirect(originalRequestedUrl);
               }
            }
         }
         catch (Exception e)
         {
            Logger.info("Error while trying to redirect to the referer after login", e);
         }
         return result;
      }
   }

   /**
    * Logout and clean the session.
    * 
    * @return redirect Application.index()
    */
   @SubjectPresent
   public static Result logout()
   {
      return PlayAuthenticate.logout(session());
   }

   /**
    * Handle register form submission.
    * 
    * @return registration-view (badRequest)
    * @return completed-view
    */
   @SubjectNotPresent
   public static Result register()
   {
      Form<SimpleAuthSignup> registrationForm = UserForms.RegistrationForm().bindFromRequest();
      if (registrationForm.hasErrors())
      {
         flashValidationErrors(registrationForm);
         return badRequest(views.html.registration.render(UserForms.LoginForm(), registrationForm));
      }
      else
      {
         return UsernamePasswordAuthProvider.handleSignup(ctx());
      }
   }

   private static void flashValidationErrors(final Form<?> form)
   {
      if (!form.hasErrors()) return;

      for (Entry<String, List<play.data.validation.ValidationError>> error : form.errors().entrySet())
      {
         if (error.getKey() == null || error.getKey().isEmpty()) continue;

         StringBuilder sb = new StringBuilder();
         List<ValidationError> errors = error.getValue();
         for (int i = 0; i < errors.size(); i++)
         {
            ValidationError validationError = errors.get(i);
            sb.append(Messages.get(ctx().lang(), validationError.message(), validationError.arguments()));
            if (i + 1 < errors.size())
            {
               sb.append(", ");
            }
         }
         flash(error.getKey(), String.format("Fehler: %s", sb.toString()));
      }
   }

   public static Result unverified()
   {
      com.feth.play.module.pa.controllers.Authenticate.noCache(response());
      return ok(views.html.account.unverified.render());
   }

   public static Result userExists()
   {
      com.feth.play.module.pa.controllers.Authenticate.noCache(response());
      return ok(views.html.account.exists.render());
   }

   public static Result verify(final String token)
   {
      com.feth.play.module.pa.controllers.Authenticate.noCache(response());
      final Token emailVerifyToken = SimpleAuthUtil.tokenIsValid(token, Type.EMAIL_VERIFICATION);
      if (emailVerifyToken != null)
      {
         final IUser user = User.findUserByID(emailVerifyToken.targetUserID);
         SimpleAuthUtil.verifyUser(user);
         flash("success", String.format("E-Mail-Adresse (%s) wurde erfolgreich verifiziert.", user.getEmail()));
         return redirect(routes.Application.index());
      }

      final Token resetPasswordToken = SimpleAuthUtil.tokenIsValid(token, Type.PASSWORD_RESET);
      if (resetPasswordToken != null)
      {
         final User user = User.findUserByID(resetPasswordToken.targetUserID);
         return ok(views.html.user.resetPassword.render(user, resetPasswordToken));
      }

      return badRequest(views.html.account.no_token_or_invalid.render(UserForms.LoginForm()));
   }

   public static Result resetPassword()
   {
      Form<EmailForm> emailForm = SimpleForms.EmailForm().bindFromRequest();
      if (!emailForm.hasErrors())
      {
         final IUser user = User.findUserByEmail(emailForm.get().email);
         if (user != null)
         {
            if (!user.isEmailValidated())
            {
               flash("error", "E-Mail-Adresse noch nicht validiert");
               return redirect(routes.Application.index());
            }
            else
            {
               SimpleUserPassAuthProvider.getProvider().sendPasswordResetMailing(user, ctx());
               flash("success", "E-Mail zum Passwort neu setzen wurde gesendet.");
               return redirect(routes.Application.index());
            }
         }
         else
         {
            flash("error", "E-Mail-Adresse nicht registriert");
            return redirect(routes.Application.index());
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   public static Result resetPasswordSave(final String token)
   {
      final Token ta = SimpleAuthUtil.tokenIsValid(token, Type.PASSWORD_RESET);
      if (ta == null)
      {
         return badRequest(views.html.account.no_token_or_invalid.render(UserForms.LoginForm()));
      }
      final User user = User.findUserByID(ta.targetUserID);

      if (user != null)
      {
         Form<PasswordResetForm> passwordForm = UserForms.PasswordResetForm().bindFromRequest();
         if (passwordForm.hasErrors())
         {
            ErrorHelper.handleErrors(passwordForm, new ErrorHandler() {
               @Override
               public void handleError(String identifier, ValidationError error)
               {
                  flash(identifier, error.message());
               }
            });
         }
         else
         {
            UserUtil.changePassword(user, passwordForm.get().password);
            flash("success", String.format("Passwort wurde erfolgreich geändert.", user.getEmail()));
            ta.delete();
            return redirect(routes.Application.index());
         }
      }
      return forbidden(views.html.errors.e403.render());
   }
   
   /**
    * Shows the message with id idM
    * 
    * @return showMessage-view
    */
   @SubjectPresent
   public static Result showMessage(long idM)
   {
      Message message = Message.findMessageByID(idM);
      if (message != null)
      {
         return ok(views.html.messages.showMessage.render(message));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
}
