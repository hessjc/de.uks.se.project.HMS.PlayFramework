package provider;

import static play.data.Form.form;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.Token;
import play.Application;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Call;
import play.mvc.Http.Context;
import util.CallAuthFactory;
import util.Constants;
import util.IUser;
import util.UserFactory;

import com.feth.play.module.mail.Mailer.Mail.Body;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.UsernamePassword;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;

public class SimpleUserPassAuthProvider
		extends
		UsernamePasswordAuthProvider<String, SimpleAuthUser, SimpleAuthUser, SimpleUserPassAuthProvider.SimpleLogin, SimpleUserPassAuthProvider.SimpleAuthSignup>
{

	/** Workaround to be able to extract the key to constants **/
	public static final String SETTING_KEY_EMAIL = SETTING_KEY_MAIL;

	@Override
	protected List<String> neededSettingKeys()
	{
		final List<String> needed = new ArrayList<String>(super.neededSettingKeys());
		needed.add(Constants.SETTING_KEY_VERIFICATION_LINK_SECURE);
		needed.add(Constants.SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
		needed.add(Constants.SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
		return needed;
	}

	public static SimpleUserPassAuthProvider getProvider()
	{
		return (SimpleUserPassAuthProvider) PlayAuthenticate.getProvider(UsernamePasswordAuthProvider.PROVIDER_KEY);
	}

	public static class SimpleLogin implements UsernamePassword
	{
		@Required
		private String login;

		@Required
		private String password;

		@Override
		public String getPassword()
		{
			return password;
		}

		@Override
		public String getEmail()
		{
			return login;
		}

		public String validate()
		{
			// IUser user =
			// SimpleUserPassAuthProvider.getProvider().userFactory.findByLoginPassword(login,
			// loginPassword);
			// if(user == null)
			// {
			// return "Sorry, but no user with these credentials can be found!";
			// }
			// else if(!user.isEmailValidated())
			// {
			// return "Sorry, but you first have to validate your account!";
			// }
			return null;
		}

		public String getLogin()
		{
			return login;
		}

		public void setLogin(String login)
		{
			this.login = login;
		}

		public void setPassword(String password)
		{
			this.password = password;
		}
	}

	public static class SimpleAuthSignup implements UsernamePassword
	{
		@Required
		private String firstName;

		@Required
		private String lastName;

		private String matrikelNumber;

		@Required
		@MinLength(5)
		private String signupPassword;

		@Required
		@MinLength(5)
		private String repeatSignupPassword;

		@Required
		@Email
		private String signupEmail;

		@Required
		@Email
		private String repeatSignupEmail;

		public String getEmail()
		{
			return signupEmail;
		}

		public String validate()
		{
			if (signupPassword == null || !signupPassword.equals(repeatSignupPassword))
			{
				return "Die Passwörter stimmen nicht überein";
			} else if (signupEmail == null || !signupEmail.equals(repeatSignupEmail))
			{
				return "Die E-Mail Adressen stimmen nicht überein";
			}
			return null;
		}

		@Override
		public String getPassword()
		{
			return signupPassword;
		}

		public String getFirstName()
		{
			return firstName;
		}

		public void setFirstName(String firstName)
		{
			this.firstName = firstName;
		}

		public String getLastName()
		{
			return lastName;
		}

		public void setLastName(String lastName)
		{
			this.lastName = lastName;
		}

		public String getMatrikelNumber()
		{
			return matrikelNumber;
		}

		public void setMatrikelNumber(String matrikelNumber)
		{
			this.matrikelNumber = matrikelNumber;
		}

		public String getSignupPassword()
		{
			return signupPassword;
		}

		public void setSignupPassword(String signupPassword)
		{
			this.signupPassword = signupPassword;
		}

		public String getRepeatSignupPassword()
		{
			return repeatSignupPassword;
		}

		public void setRepeatSignupPassword(String repeatSignupPassword)
		{
			this.repeatSignupPassword = repeatSignupPassword;
		}

		public String getSignupEmail()
		{
			return signupEmail;
		}

		public void setSignupEmail(String signupEmail)
		{
			this.signupEmail = signupEmail;
		}

		public String getRepeatSignupEmail()
		{
			return repeatSignupEmail;
		}

		public void setRepeatSignupEmail(String repeatSignupEmail)
		{
			this.repeatSignupEmail = repeatSignupEmail;
		}
	}

	public static final Form<SimpleAuthSignup> SIGNUP_FORM = form(SimpleAuthSignup.class);
	public static final Form<SimpleLogin> LOGIN_FORM = form(SimpleLogin.class);

	private UserFactory userFactory;

	private CallAuthFactory callAuthFactory;

	public SimpleUserPassAuthProvider(Application app)
	{
		super(app);
	}

	protected Form<SimpleAuthSignup> getSignupForm()
	{
		return SIGNUP_FORM;
	}

	protected Form<SimpleLogin> getLoginForm()
	{
		return LOGIN_FORM;
	}

	@Override
	public void onStart()
	{
		Configuration configuration = getApplication().configuration();
		this.userFactory = createFromConfiguration(configuration, Constants.SIMPLE_AUTH_USER_FACTORY_KEY);
		this.callAuthFactory = createFromConfiguration(configuration, Constants.SIMPLE_AUTH_CALL_AUTH_FACTORY_KEY);

		PlayAuthenticate.setResolver(new Resolver()
		{

			@Override
			public Call login()
			{
				return callAuthFactory.onLogin();
			}

			@Override
			public Call afterAuth()
			{
				return callAuthFactory.afterAuth();
			}

			@Override
			public Call afterLogout()
			{
				return callAuthFactory.afterLogout();
			}

			@Override
			public Call auth(final String provider)
			{
				return callAuthFactory.onAuth(provider);
			}

			@Override
			public Call askMerge()
			{
				// Unsupported
				return null;
			}

			@Override
			public Call askLink()
			{
				// Unsupported
				return null;
			}

			@Override
			public Call onException(final AuthException e)
			{
				return callAuthFactory.onException(e);
			}
		});

		super.onStart();
	}

	public UserFactory getUserFactory()
	{
		return userFactory;
	}

	public CallAuthFactory getCallAuthFactory()
	{
		return callAuthFactory;
	}

	@SuppressWarnings("unchecked")
	private <T> T createFromConfiguration(final Configuration configuration, String key)
	{
		String classFQN = configuration.getString(key);

		if (classFQN == null)
		{
			throw configuration.reportError(key, String.format("Error: Key '%s' cannot be found!", key), null);
		} else
		{
			try
			{
				return (T) Class.forName(classFQN, true, getApplication().classloader()).newInstance();
			} catch (Exception e)
			{
				throw configuration.reportError(key, String.format("Error instantiating class '%s'.", classFQN), null);
			}
		}
	}

	@Override
	protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.SignupResult signupUser(final SimpleAuthUser user)
	{
		IUser u = this.userFactory.findByAuthUser(user);
		if (u != null)
		{
			if (u.isEmailValidated())
			{
				// This user exists, has its email validated and is active
				return SignupResult.USER_EXISTS;
			} else
			{
				// this user exists, is active but has not yet validated its
				// email
				return SignupResult.USER_EXISTS_UNVERIFIED;
			}
		}

		// Create a new user
		this.userFactory.create(user);
		// Usually the email should be verified before allowing login, however
		// if you return
		// return SignupResult.USER_CREATED;
		// then the user gets logged in directly
		return SignupResult.USER_CREATED_UNVERIFIED;
	}

	@Override
	protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.LoginResult loginUser(final SimpleAuthUser user)
	{
		final IUser u = this.userFactory.findByAuthUser(user);
		if (u == null)
		{
			return LoginResult.NOT_FOUND;
		} else
		{
			if (!u.isEmailValidated())
			{
				return LoginResult.USER_UNVERIFIED;
			} else
			{
				if (this.userFactory.checkPassword(user, u))
				{
					// Password was correct
					return LoginResult.USER_LOGGED_IN;
				} else
				{
					// if you don't return here,
					// you would allow the user to have
					// multiple passwords defined
					// usually we don't want this
					return LoginResult.WRONG_PASSWORD;
				}
			}
		}
	}

	@Override
	protected Call userExists(final UsernamePasswordAuthUser authUser)
	{
		return this.callAuthFactory.onUserExists(authUser);
	}

	@Override
	protected Call userUnverified(final UsernamePasswordAuthUser authUser)
	{
		return this.callAuthFactory.onUserUnverified(authUser);
	}

	@Override
	protected SimpleAuthUser buildSignupAuthUser(final SimpleAuthSignup signup, final Context ctx)
	{
		return new SimpleAuthUser(signup);
	}

	@Override
	protected SimpleAuthUser buildLoginAuthUser(final SimpleLogin login, final Context ctx)
	{
		return new SimpleAuthUser(login.getEmail(), login.getPassword());
	}

	@Override
	protected SimpleAuthUser transformAuthUser(final SimpleAuthUser authUser, final Context context)
	{
		return new SimpleAuthUser(authUser.getEmail(), null);
	}

	@Override
	protected String getVerifyEmailMailingSubject(final SimpleAuthUser user, final Context ctx)
	{
		return Messages.get("playauthenticate.password.verify_signup.subject");
	}

	@Override
	protected String onLoginUserNotFound(final Context context)
	{
		context.flash().put(Constants.FLASH_ERROR_KEY, "Der Benutzer wurde nicht gefunden oder das Passwort stimmt nicht überein.");
		return super.onLoginUserNotFound(context);
	}

	private String validateEmailTemplatePath(String templatePath, String langCode)
	{
		String tmp = templatePath;

		// Normalize
		if (templatePath.contains("views.html"))
		{
			tmp = tmp.replaceFirst("views.html", "views.%s");
		}
		if (templatePath.contains("views.txt"))
		{
			tmp = tmp.replaceFirst("views.txt", "views.%s");
		}

		// Ensure that we have both html and txt representations
		Class<?> loadTemplateClass = loadTemplateClass(String.format(tmp, "html"), langCode);
		if (loadTemplateClass == null)
		{
			return null;
		}
		loadTemplateClass = loadTemplateClass(String.format(tmp, "txt"), langCode);
		if (loadTemplateClass == null)
		{
			return null;
		}
		return tmp;
	}

	private String readConfigurationKey(final String key)
	{
		return getApplication().configuration().getString(key);
	}

	private static String generateToken()
	{
		return UUID.randomUUID().toString();
	}

	@Override
	protected String generateVerificationRecord(final SimpleAuthUser user)
	{
		return generateVerificationRecord(this.userFactory.findByAuthUser(user));
	}

	protected String generateVerificationRecord(final IUser user)
	{
		if (user != null)
		{
			final String token = generateToken();

			// Do database actions, etc.
			Token.create(Token.Type.EMAIL_VERIFICATION, token, user);
			return token;
		}
		return null;
	}

	protected String generatePasswordResetRecord(final IUser u)
	{
		final String token = generateToken();
		Token.create(Token.Type.PASSWORD_RESET, token, u);
		return token;
	}

	protected String getPasswordResetMailingSubject(final IUser user, final Context ctx)
	{
		return Messages.get("playauthenticate.password.reset_email.subject");
	}

	protected Body getPasswordResetMailingBody(final String token, final IUser user, final Context ctx)
	{
		return getEmailBody(token, user.getName(), user.getEmail(), ctx, Constants.SIMPLE_AUTH_PASSWORD_RESET_EMAIL_TEMPLATE_BODY_KEY,
				Constants.DEFAULT_PASSWORD_RESET_EMAIL_TEMPLATE_PATH);
	}

	protected Body getVerifyEmailMailingBodyAfterSignup(final String token, final IUser user, final Context ctx)
	{
		return getEmailBody(token, user.getName(), user.getEmail(), ctx, Constants.SIMPLE_AUTH_VERIFCATION_EMAIL_TEMPLATE_BODY_KEY,
				Constants.DEFAULT_VERIFICATION_EMAIL_TEMPLATE_PATH);
	}

	private Body getEmailBody(final String token, final String userName, String userEmail, final Context ctx,
			final String templateConfigurationKey, final String defaultTemplatePath)
	{
		final boolean isSecure = getConfiguration().getBoolean(Constants.SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = this.callAuthFactory.onUserVerify(token).absoluteURL(ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		String templatePath = readConfigurationKey(templateConfigurationKey);

		if (templatePath != null)
		{
			templatePath = validateEmailTemplatePath(templatePath, langCode);
		}

		if (templatePath == null)
		{
			templatePath = defaultTemplatePath;
		}

		final String html = getEmailTemplate(String.format(templatePath, "html"), langCode, url, token, userName, userEmail);
		final String text = getEmailTemplate(String.format(templatePath, "txt"), langCode, url, token, userName, userEmail);

		return new Body(text, html);
	}

	@Override
	protected Body getVerifyEmailMailingBody(final String token, final SimpleAuthUser user, final Context ctx)
	{
		return getEmailBody(token, user.getName(), user.getEmail(), ctx, Constants.SIMPLE_AUTH_SIGNUP_VERIFCATION_EMAIL_TEMPLATE_BODY_KEY,
				Constants.DEFAULT_SIGNUP_VERIFICATION_EMAIL_TEMPLATE_PATH);
	}

	public void sendPasswordResetMailing(final IUser user, final Context ctx)
	{
		final String token = generatePasswordResetRecord(user);
		final String subject = getPasswordResetMailingSubject(user, ctx);
		final Body body = getPasswordResetMailingBody(token, user, ctx);
		mailer.sendMail(subject, body, getEmailName(user));
	}

	public boolean isLoginAfterPasswordReset()
	{
		return getConfiguration().getBoolean(Constants.SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
	}

	protected String getVerifyEmailMailingSubjectAfterSignup(final IUser user, final Context ctx)
	{
		return Messages.get("playauthenticate.password.verify_email.subject");
	}

	protected String getEmailTemplate(final String template, final String langCode, final String url, final String token,
			final String name, final String email)
	{
		Class<?> cls = loadTemplateClass(template, langCode);
		String ret = null;
		if (cls != null)
		{
			Method htmlRender = null;
			try
			{
				htmlRender = cls.getMethod("render", String.class, String.class, String.class, String.class);
				ret = htmlRender.invoke(null, url, token, name, email).toString();

			} catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			} catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}

	private Class<?> loadTemplateClass(final String template, final String langCode)
	{
		Class<?> cls = null;
		try
		{
			cls = Class.forName(template + "_" + langCode);
		} catch (ClassNotFoundException e)
		{
			Logger.warn("Template: '" + template + "_" + langCode + "' was not found! Trying to use English fallback template instead.");
		}
		if (cls == null)
		{
			try
			{
				cls = Class.forName(template + "_" + Constants.EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
			} catch (ClassNotFoundException e)
			{
				Logger.error("Fallback template: '" + template + "_" + Constants.EMAIL_TEMPLATE_FALLBACK_LANGUAGE
						+ "' was not found either!");
			}
		}
		return cls;
	}

	public void sendVerifyEmailMailingAfterSignup(final IUser user, final Context ctx)
	{

		final String subject = getVerifyEmailMailingSubjectAfterSignup(user, ctx);
		final String token = generateVerificationRecord(user);
		final Body body = getVerifyEmailMailingBodyAfterSignup(token, user, ctx);
		mailer.sendMail(subject, body, getEmailName(user));
	}

	private String getEmailName(final IUser user)
	{
		return getEmailName(user.getEmail(), user.getName());
	}
}
