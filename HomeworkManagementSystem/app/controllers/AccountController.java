package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import models.Duty;
import models.Lecture;
import models.Roles;
import models.Semester;
import models.User;
import play.api.mvc.Call;
import play.api.templates.Html;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ErrorHelper;
import utils.ErrorHelper.ErrorHandler;
import utils.PlayHelper;
import utils.UserUtil;
import be.objectify.deadbolt.java.actions.Dynamic;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import forms.AccountForms;
import forms.AccountForms.NameForm;
import forms.AccountForms.PasswordForm;
import forms.AccountForms.StudentForm;

/**
 * Homework Management System - Important Account Information
 * Account controller class for sending information to views
 * 
 * @version 1.0
 */
@SubjectPresent
public class AccountController extends Controller
{

   /**
    * shows my account information
    * 
    * @return my-account-view
    */
   @Dynamic("User")
   public static Result showMyAccountInfo()
   {
      return ok(views.html.account.myaccount.render(User.findBySession(session())));
   }

   /**
    * Edit users matriculation number
    * 
    * @return special edit-user-student-view
    */
   @Dynamic("PersonalInformation")
   public static Result editMatriculationNumber(Long idU)
   {
      User user = User.findUserByID(idU);
      if (user != null)
      {
         if (PlayHelper.isPjaxRequest(request()))
         {
            return ok(views.html.account.userinfo.edit.editMatriculationNumber.render(user));
         }
         else
         {
            return ok(views.html.account.myaccount.render(user));
         }
      }
      else
      {
         return errorAndReturnToIndex();
      }
   }

   private static Result errorAndReturnToIndex()
   {
      flash("error", "User does not exists");
      if (PlayHelper.isPjaxRequest(request()))
      {
         PlayHelper.setPjaxResponseURL(response(), routes.AccountController.showMyAccountInfo().absoluteURL(request()));
         return ok(views.html.index.render());
      }
      else
      {
         return badRequest(views.html.index.render());
      }
   }

   /**
    * Edit users password
    * 
    * @return
    */
   @Dynamic("PersonalInformation")
   public static Result editPassword(Long idU)
   {
      User user = User.findUserByID(idU);
      if (user != null)
      {
         if (PlayHelper.isPjaxRequest(request()))
         {
            return ok(views.html.account.userinfo.edit.editPassword.render(user));
         }
         else
         {
            return ok(views.html.account.myaccount.render(user));
         }
      }
      else
      {
         return errorAndReturnToIndex();
      }
   }

   /**
    * Save users password
    * 
    * @return save-users-password
    */
   @Dynamic("PersonalInformation")
   public static Result editPasswordSave(Long idU)
   {
      User user = User.findUserByID(idU);
      Form<PasswordForm> passwordForm = AccountForms.PasswordForm().bindFromRequest();
      if (user != null)
      {
         boolean hasErrors = passwordForm.hasErrors();
         if (hasErrors)
         {
            ErrorHelper.handleErrors(passwordForm, new ErrorHandler() {
               @Override
               public void handleError(String identifier, ValidationError error)
               {
                  flash(identifier, error.message());
               }
            });
            PlayHelper.pjaxFormError(response());
         }
         else
         {
            UserUtil.changePassword(user, passwordForm.get().password);
         }

         if (PlayHelper.isPjaxRequest(request()))
         {
            return hasErrors ? ok(views.html.account.userinfo.edit.editPassword.render(user)) :
                  ok(views.html.account.userinfo.password.render(user));
         }
         else
         {
            Html content = views.html.account.myaccount.render(user);
            return hasErrors ? badRequest(content) : ok(content);
         }
      }
      else
      {
         return errorAndReturnToIndex();
      }
   }

   /**
    * Save users matrikel number
    * 
    * @return save-users-matrikelnumber
    */
   @Dynamic("PersonalInformation")
   public static Result editMatriculationNumberSave(Long idU)
   {
      User user = User.findUserByID(idU);
      Form<StudentForm> studentForm = AccountForms.StudentForm().bindFromRequest();
      if (user != null)
      {
         boolean hasErrors = studentForm.hasErrors();
         if (hasErrors)
         {
            ErrorHelper.handleErrors(studentForm, new ErrorHandler() {
               @Override
               public void handleError(String identifier, ValidationError error)
               {
                  flash(identifier, error.message());
               }
            });
         }
         else
         {
            //update the session, because the session identifier may be changed here
            session().put("pa.u.id", studentForm.get().matrikelNumber);
            user.setMatrikelNumber(studentForm.get().matrikelNumber);
            if (user.getRole().equals(Roles.findRoleByName("User")))
            {
               user.setRole(Roles.findRoleByName("Student"));
            }
            // TODO Rename directory of the users' uploads
            user.update();
         }

         if (PlayHelper.isPjaxRequest(request()))
         {
            return hasErrors ? ok(views.html.account.userinfo.edit.editMatriculationNumber.render(user)) :
                  ok(views.html.account.userinfo.matriculationNumber.render(user));
         }
         else
         {
            Html content = views.html.account.myaccount.render(user);
            return hasErrors ? badRequest(content) : ok(content);
         }
      }
      else
      {
         return errorAndReturnToIndex();
      }
   }

   /**
    * Edit users email address
    * 
    * TODO: Enable this as soon as the modification of mail adresses is supported
    * 
    * @return special edit-user-email-view
    */
   @Dynamic("PersonalInformation")
   public static Result editEmail(Long idU)
   {
      //      User user = User.findUserByID(idU);
      //      if(user != null) {
      //         Form<EmailForm> form = AccountForms.EmailForm();
      //         form = form.fill(new EmailForm(user.getId, user.email));
      //         return ok(views.html.account.userinfo.editemail.render(UserForms.LoginForm(), form, user));
      //      } else {
      //         flash("error", "User does not exists");
      //         return badRequest(views.html.index.render());
      //      }
      return TODO;
   }

   /**
    * Save users email address
    * 
    * TODO: Enable this as soon as the modification of mail adresses is supported
    * 
    * @return save-user-email
    */
   @Dynamic("PersonalInformation")
   public static Result editEmailSave(Long idU)
   {
      //      User user = User.findUserByID(idU);
      //      Form<EmailForm> emailForm = AccountForms.EmailForm().bindFromRequest();
      //      if(user != null) {
      //         if (emailForm.hasErrors()) {
      //            ErrorHelper.handleErrors(emailForm, new ErrorHandler() {
      //               @Override
      //               public void handleError(String identifier, ValidationError error) {
      //                  flash(identifier, error.message());
      //               }
      //            });
      //            return badRequest(views.html.account.userinfo.editemail.render(UserForms.LoginForm(), AccountForms.EmailForm(), user));
      //         } else {
      //               //update the session, because the session identifier may be changed here
      //               session().put("pa.u.id", emailForm.get().emailNew);
      //            user.email = emailForm.get().emailNew;
      //            user.update();
      //            return ok(views.html.account.myaccount.render(UserForms.LoginForm(), user));
      //         }
      //      } else {
      //         flash("error", "User does not exists");
      //         return badRequest(views.html.index.render());
      //      }
      return TODO;
   }

   /**
    * Edit users name information
    * 
    * @return special edit-user-name-view
    */
   @Dynamic("PersonalInformation")
   public static Result editName(Long idU)
   {
      User user = User.findUserByID(idU);
      if (user != null)
      {
         Form<NameForm> form = AccountForms.NameForm();
         form = form.fill(new NameForm(user.getId(), user.getFirstName(), user.getLastName()));

         if (PlayHelper.isPjaxRequest(request()))
         {
            return ok(views.html.account.userinfo.edit.editName.render(user));
         }
         else
         {
            return ok(views.html.account.myaccount.render(user));
         }
      }
      else
      {
         return errorAndReturnToIndex();
      }
   }

   /**
    * Save users name information
    * 
    * @return save-users-name-info
    */
   @Dynamic("PersonalInformation")
   public static Result editNameSave(Long idU)
   {
      User user = User.findUserByID(idU);
      Form<NameForm> nameForm = AccountForms.NameForm().bindFromRequest();
      if (user != null)
      {
         if (nameForm.hasErrors())
         {
            ErrorHelper.handleErrors(nameForm, new ErrorHandler() {
               @Override
               public void handleError(String identifier, ValidationError error)
               {
                  flash(identifier, error.message());
               }
            });
            return badRequest(views.html.account.userinfo.edit.editName.render(user));
         }
         else
         {
            user.setFirstName(nameForm.get().firstName);
            user.setLastName(nameForm.get().lastName);
            //update the session, because the session identifier may be changed here
            user.update();

            flash("success", "Daten erfolgreich geändert!");

            Call showAccCall = routes.AccountController.showMyAccountInfo();
            if (PlayHelper.isPjaxRequest(request()))
            {
               PlayHelper.setPjaxResponseURL(response(), showAccCall.absoluteURL(request()));
            }
            return ok(views.html.account.myaccount.render(user));
         }
      }
      else
      {
         return errorAndReturnToIndex();
      }
   }

   /**
    * shows my lectures (for students, lectureadmins, proofreaders and administrators)
    * 
    * @return my-lectures-view
    */
   @Dynamic("Participant")
   public static Result showMyLectures()
   {
      return ok(views.html.account.mylectures.render());
   }

   /**
    * shows my assessments (for students, lecture administrators, proofreaders and administrators)
    * 
    * @return my-assessments-view
    */
   @Dynamic("Participant")
   public static Result showMyAssessments()
   {
      return ok(views.html.account.myassessments.render());
   }
   
   /**
    * shows my messages (for students, lecture administrators, proofreaders and administrators)
    * 
    * @return my-assessments-view
    */
   @Dynamic("Participant")
   public static Result showMyMessages()
   {
      return ok(views.html.account.mymessages.render());
   }

   /**
    * shows my dutys (for students, lecture administrators, proofreaders and administrators)
    * 
    * @return my-dutys-view
    */
   @Dynamic("Proofreader")
   public static Result showMyDutys()
   {
      return ok(views.html.account.mydutys.render());
   }

   @Dynamic("IsParticipant")
   public static Map<Semester, Collection<Lecture>> myParticipantLectures()
   {
      SortedMap<Semester, Collection<Lecture>> lectures = new TreeMap<Semester, Collection<Lecture>>();
      for (Lecture lecture : User.findBySession(session()).getLectures())
      {
         Collection<Lecture> lecturesPerSemester = lectures.get(lecture.getSemester());
         if (lecturesPerSemester == null)
         {
            lecturesPerSemester = new LinkedList<Lecture>();
            lectures.put(lecture.getSemester(), lecturesPerSemester);
         }
         lecturesPerSemester.add(lecture);
      }
      return lectures;
   }

   @Dynamic("IsProofreader")
   public static Map<Semester, Collection<Lecture>> myProofreaderLectures()
   {
      SortedMap<Semester, Collection<Lecture>> lectures = new TreeMap<Semester, Collection<Lecture>>();
      for (Lecture lecture : User.findBySession(session()).isProofreaderOf())
      {
         Collection<Lecture> lecturesPerSemester = lectures.get(lecture.getSemester());
         if (lecturesPerSemester == null)
         {
            lecturesPerSemester = new LinkedList<Lecture>();
            lectures.put(lecture.getSemester(), lecturesPerSemester);
         }
         lecturesPerSemester.add(lecture);
      }
      return lectures;
   }

   @Dynamic("IsLectureadmin")
   public static Map<Semester, Collection<Lecture>> myLectureadminLectures()
   {
      SortedMap<Semester, Collection<Lecture>> lectures = new TreeMap<Semester, Collection<Lecture>>();
      for (Lecture lecture : User.findBySession(session()).isLectureadminOf())
      {
         Collection<Lecture> lecturesPerSemester = lectures.get(lecture.getSemester());
         if (lecturesPerSemester == null)
         {
            lecturesPerSemester = new LinkedList<Lecture>();
            lectures.put(lecture.getSemester(), lecturesPerSemester);
         }
         lecturesPerSemester.add(lecture);
      }
      return lectures;
   }

   @Dynamic("Participant")
   public static List<Duty> myDutys()
   {
      User user = User.findBySession(session());
      ArrayList<Duty> dutys = new ArrayList<Duty>();
      for(Duty duty: user.getDutys()) 
      {
         dutys.add(duty);
      }
      Collections.sort(dutys);
      return dutys;
   }
   
   @Dynamic("Participant")
   public static List<Semester> mySemester()
   {
      User user = User.findBySession(session());
      Set<Semester> unsortedSemester = new LinkedHashSet<Semester>();
      for(Duty duty: user.getDutys()) 
      {
         unsortedSemester.add(duty.getAssignment().getLecture().getSemester());
      }
      ArrayList<Semester> sortedSemester = new ArrayList<Semester>();
      for(Semester semester: unsortedSemester) 
      {
         sortedSemester.add(semester);
      }
      Collections.sort(sortedSemester);
      return sortedSemester;
   }
   
   @Dynamic("Participant")
   public static List<Lecture> myLectures()
   {
      User user = User.findBySession(session());
      Set<Lecture> unsortedLectures = new LinkedHashSet<Lecture>();
      for(Duty duty: user.getDutys()) 
      {
         unsortedLectures.add(duty.getAssignment().getLecture());
      }
      ArrayList<Lecture> lectures = new ArrayList<Lecture>();
      for(Lecture lecture: unsortedLectures) 
      {
         lectures.add(lecture);
      }
      Collections.sort(lectures);
      return lectures;
   }
}
