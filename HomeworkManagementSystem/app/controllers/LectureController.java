package controllers;

import java.text.ParseException;

import models.Bonus;
import models.Lecture;
import models.Message;
import models.Semester;
import models.User;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import security.HDSDynamicResourceHandler;
import utils.DateHelper;
import utils.ErrorHelper;
import utils.ErrorHelper.ErrorHandler;
import utils.MailUtil;
import utils.StringHelper;
import be.objectify.deadbolt.java.actions.Dynamic;
import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.avaje.ebean.Ebean;

import forms.AssignmentForms;
import forms.MessageForms.AnswerForm;
import forms.MessageForms;
import forms.MessageForms.MessageForm;
import forms.LectureForms;
import forms.LectureForms.CreateLectureForm;
import forms.LectureForms.EditLectureForm;
import forms.LectureForms.SendEmailToAllForm;
import forms.SimpleForms;
import forms.SimpleForms.IDForm;

@SubjectPresent
public class LectureController extends Controller
{

   /**
    * render the view with all lectures
    * 
    * @return lectures-view
    */
   @Dynamic("User")
   public static Result showLectures()
   {
      return ok(views.html.lecture.lectures.render());
   }

   /**
    * render the show-lecture-view of the lecture with the given id
    * 
    * @param id
    * @return show-lecture-view
    */
   @Dynamic("User")
   public static Result showLecture(Long id)
   {
      Lecture lecture = Lecture.findLectureByID(id);
      if (lecture != null)
      {
         return ok(views.html.lecture.showlecture.render(User.findBySession(session()), lecture));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * render the create-lecture-view
    * 
    * @return create-lecture-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result createLecture()
   {
      return ok(views.html.lecture.createLecture.render(LectureForms.CreateLectureForm()));
   }

   /**
    * save a correct created lecture or handles possible mistakes
    * 
    * @return create-lecture-view (badRequest)
    * @return show-lecture-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result saveLectureCreate()
   {
      Form<CreateLectureForm> createLectureForm = LectureForms.CreateLectureForm().bindFromRequest();
      if (createLectureForm.hasErrors())
      {
         ErrorHelper.handleErrors(createLectureForm, new ErrorHandler() {
            @Override
            public void handleError(String identifier, ValidationError error)
            {
               flash(identifier, error.message());
            }
         });
         return badRequest(views.html.lecture.createLecture.render(createLectureForm));
      }
      else
      {
         Lecture lecture = Lecture.createLecture(createLectureForm.get().name,
               createLectureForm.get().closingdate,
               createLectureForm.get().description,
               Semester.findSemester(createLectureForm.get().semester),
               createLectureForm.get().lectureadmin);
         lecture.setOptionalDutys(createLectureForm.get().optionalDutys);
         lecture.setLowerProcentualBounderyOfDutys(createLectureForm.get().lowerProcentualBounderyOfDutys);
         lecture.setMinimumPercentageForExamination(createLectureForm.get().minimumPercentageForExamination);
         lecture.update();

         String[] stringArray = StringHelper.splitStringWithWhitespaces(createLectureForm.get().bonuses);
         if (stringArray!=null && stringArray.length>1)
         {
            System.out.println(stringArray.length);
            for (int i = 0; i < stringArray.length; i = i + 2)
            {
               Bonus.createBonus(Integer.valueOf(stringArray[i]), Float.valueOf(stringArray[i + 1]), lecture);
            }
         }
         return redirect(routes.LectureController.showLecture(lecture.getId()));
      }
   }

   /**
    * render the edit-lecture-view of the lecture with the given id
    * 
    * @param id
    * @return edit-lecture-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result editLecture(Long id)
   {
      Lecture lecture = Lecture.findLectureByID(id);
      if (lecture != null)
      {
         Form<EditLectureForm> form = LectureForms.EditLectureForm();
         form = form.fill(new EditLectureForm(lecture));
         return ok(views.html.lecture.editLecture.render(form, LectureForms.AddForm()));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * save the correct edited lecture with the given id or handles possible mistakes
    * 
    * @param id
    * @return edit-lecture-view (badRequest) or show-lecture-view
    * @throws ParseException
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result saveLectureEdit(Long id) throws ParseException
   {
      Lecture lecture = Lecture.findLectureByID(id);
      Form<EditLectureForm> editLectureForm = LectureForms.EditLectureForm().bindFromRequest();
      if (editLectureForm.hasErrors())
      {
         ErrorHelper.handleErrors(editLectureForm, new ErrorHandler() {
            @Override
            public void handleError(String identifier, ValidationError error)
            {
               flash(identifier, error.message());
            }
         });
         editLectureForm = editLectureForm.fill(new EditLectureForm(lecture));
         return badRequest(views.html.lecture.editLecture.render(editLectureForm,
               LectureForms.AddForm()));
      }
      else
      {
         Lecture.editLecture(lecture, editLectureForm.get().name,
               editLectureForm.get().semester, editLectureForm.get().closingdate,
               editLectureForm.get().description);
         return redirect(routes.LectureController.showLecture(id));
      }
   }

   /**
    * add a chosen lecture administrator to the lecture with the given idL
    * 
    * @param idL
    * @return edit-lecture-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result addLectureadmin(Long idL)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      User user = User.findUser(LectureForms.AddForm().bindFromRequest().get().name);
      flash("lectureadmin", user.getLastName() + ", " + user.getFirstName() + "(" + user.getEmail()
            + ") als Lectureadmin hinzugefügt.");
      Lecture.addLectureadminToLecture(lecture, user);
      return redirect(routes.LectureController.editLecture(lecture.getId()));
   }

   /**
    * add a chosen proofreader to the lecture with the given idL
    * 
    * @param idL
    * @return edit-lecture-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result addProofreader(Long idL)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      User user = User.findUser(LectureForms.AddForm().bindFromRequest().get().name);
      flash("lectureadmin", user.getLastName() + ", " + user.getFirstName() + "(" + user.getEmail()
            + ") als Proofreader hinzugefügt.");
      Lecture.addProofreaderToLecture(lecture, user);
      return redirect(routes.LectureController.editLecture(idL));
   }

   /**
    * delete the lecture administrator with the id idLa
    * in the lecture with the id idL
    * 
    * @param idL
    * @param idLa
    * @return
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result deleteLectureadmin(Long idL)
   {
      Form<IDForm> form = SimpleForms.IDForm().bindFromRequest();
      Lecture lecture = Lecture.findLectureByID(idL);
      if (lecture != null)
      {
         if (!form.hasErrors())
         {
            User user = User.findUserByID(form.get().id);
            if (user != null && lecture.getLectureadmins().contains(user))
            {
               flash("participantDeleted",
                     Messages.get("lecture.lectureadmin.delete.success", user.getName(), user.getEmail()));
               Lecture.removeLectureadminOfLecture(lecture, user);
               return redirect(routes.LectureController.editLecture(idL));
            }
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   /**
    * delete the proofreader with the id idLa
    * in the lecture with the id idL
    * 
    * @param idL
    * @param idP
    * @return
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result deleteProofreader(Long idL)
   {
      Form<IDForm> form = SimpleForms.IDForm().bindFromRequest();
      Lecture lecture = Lecture.findLectureByID(idL);
      if (lecture != null)
      {
         if (!form.hasErrors())
         {
            User user = User.findUserByID(form.get().id);
            if (user != null && lecture.getProofreaders().contains(user))
            {
               flash("participantDeleted",
                     Messages.get("lecture.proofreader.delete.success", user.getName(), user.getEmail()));
               Lecture.removeProofreaderOfLecture(lecture, user);
               return redirect(routes.LectureController.editLecture(idL));
            }
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   /**
    * delete a lecture by id
    * 
    * @return lectures-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result deleteLecture()
   {
      Form<IDForm> form = SimpleForms.IDForm().bindFromRequest();
      if (!form.hasErrors())
      {
         long lectureID = form.get().id;
         Lecture lecture = Lecture.findLectureByID(lectureID);
         if (lecture != null)
         {
            flash("success",
                  Messages.get("lecture.delete.success", lecture.getSemester().getSemester(), lecture.getName()));
            Lecture.deleteLecture(lecture);
            return redirect(routes.LectureController.showLectures());
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   /**
    * join the lecture with the given id
    * 
    * @param id
    * @return show-lecture-view
    */
   @Dynamic("IsNotParticipantOfLecture")
   public static Result joinLecture(Long id)
   {
      Lecture lecture = Lecture.findLectureByID(id);
      if (lecture != null && !DateHelper.isExpired(lecture.getClosingdate()))
      {
         User user = User.findBySession(session());
         user.getLectures().add(lecture);
         Ebean.saveManyToManyAssociations(user, "lectures");
         return redirect(routes.LectureController.showLecture(id));
      }

      return badRequest(views.html.lecture.showlecture.render(User.findBySession(session()), lecture));
   }

   @Dynamic(HDSDynamicResourceHandler.IS_LECTUREADMIN_OF_LECTURE)
   public static Result sendMailToAll(Long lectureId)
   {
      Lecture lecture = Lecture.findLectureByID(lectureId);
      if (lecture != null)
      {
         Form<SendEmailToAllForm> form = LectureForms.SendEmailToAllForm().bindFromRequest();
         if (!form.hasErrors())
         {
            if (MailUtil
                  .sendMails(ctx(), lecture.getUsers(), String.format("%s: %s", lecture.getName(), form.get().subject),
                        MailUtil.EMPTY_EMAIL, form.get().text))
            {
               flash("success", "Die E-Mails wurden gesendet");
            }
            else
            {
               flash("error", "Die E-Mails wurden nicht gesendet");
            }
            return redirect(routes.LectureController.showLecture(lectureId));
         }
      }
      return badRequest(views.html.lecture.showlecture.render(User.findBySession(session()), lecture));
   }
   
   
   /*************************************************************************************************************************
    ***************************************************** Message ***********************************************************
    *************************************************************************************************************************/
   
   @Dynamic("IsParticipantOfLecture")
   public static Result createMessage(long idL)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      if (lecture != null)
      {
         return ok(views.html.messages.createMessage.render(lecture, null, null, MessageForms.MessageForm()));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
   
   @Dynamic("IsParticipantOfLecture")
   public static Result saveMessageCreate(long idL)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      Form<MessageForm> messageForm = MessageForms.MessageForm().bindFromRequest();
      User user = User.findBySession(session());
      if (lecture != null)
      {
         if (messageForm.hasErrors())
         {
            ErrorHelper.handleErrors(messageForm, new ErrorHandler() {
               @Override
               public void handleError(String identifier, ValidationError error)
               {
                  flash(identifier, error.message());
               }
            });
            messageForm = messageForm.fill(new MessageForms.MessageForm());
            return badRequest(views.html.messages.createMessage
                  .render(lecture, null, null, messageForm));
         }
         else
         {
            Message.createMessage(messageForm.get().text, lecture, null, null, user);
            return redirect(routes.LectureController.showLecture(idL));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
   
   @Dynamic("IsParticipantOfLecture")
   public static Result createAnswer(long idL, long idM)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      Message message = Message.findMessageByID(idM);
      Message parent = message;
      if (lecture != null && message != null)
      {
         Form<AnswerForm> answerForm = AnswerForm.AnswerForm();
         answerForm = answerForm.fill(new AnswerForm(idM));
         return ok(views.html.messages.createAnswer.render(lecture, null, null, answerForm));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
   
   @Dynamic("IsParticipantOfLecture")
   public static Result saveAnswerCreate(long idL, long idM)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      Message message = Message.findMessageByID(idM);
      Form<AnswerForm> answerForm = MessageForms.AnswerForm().bindFromRequest();
      User user = User.findBySession(session());
      if (lecture != null && message != null)
      {
         if (answerForm.hasErrors())
         {
            ErrorHelper.handleErrors(answerForm, new ErrorHandler() {
               @Override
               public void handleError(String identifier, ValidationError error)
               {
                  flash(identifier, error.message());
               }
            });
            answerForm = answerForm.fill(new AnswerForm(idM));
            return badRequest(views.html.messages.createAnswer
                  .render(lecture, null, null, answerForm));
         }
         else
         {
            Message.createAnswer(answerForm.get().text, idM, user);
            return redirect(routes.LectureController.showLecture(idL));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
}
