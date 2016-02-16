package controllers;

import models.Assignment;
import models.Duty;
import models.Lecture;
import models.Message;
import models.User;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ErrorHelper;
import utils.ErrorHelper.ErrorHandler;
import be.objectify.deadbolt.java.actions.Dynamic;
import forms.MessageForms;
import forms.MessageForms.AnswerForm;
import forms.MessageForms.MessageForm;

public class MessageController extends Controller
{
   
   @Dynamic("IsParticipantOfLecture")
   public static Result createDutyMessage(long idL, long idA, long idD)
   {
      Duty duty = Duty.findDutyByID(idD);
      if (duty != null && duty.getAssignment().getId() == idA && duty.getAssignment().getLecture().getId() == idL)
      {
         return ok(views.html.messages.createMessage.render(null, null, duty, MessageForms.MessageForm()));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
   
   @Dynamic("IsParticipantOfLecture")
   public static Result saveDutyMessageCreate(long idL, long idA, long idD)
   {
      Duty duty = Duty.findDutyByID(idD);
      Form<MessageForm> messageForm = MessageForms.MessageForm().bindFromRequest();
      User user = User.findBySession(session());
      if (duty != null && duty.getAssignment().getId() == idA && duty.getAssignment().getLecture().getId() == idL)
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
                  .render(null, null, duty, messageForm));
         }
         else
         {
            Message.createMessage(messageForm.get().text, null, null, duty, user);
            return redirect(routes.AccountController.showMyMessages());
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
   
   @Dynamic("IsParticipantOfLecture")
   public static Result createDutyAnswer(long idL, long idA, long idD, long idM)
   {
      Message message = Message.findMessageByID(idM);
      Duty duty = message.getConversationDuty();
      User user = User.findBySession(session());
      if (duty != null && duty.getAssignment().getLecture().getId() == idL 
            && message != null && duty.getAssignment().getId() == idA
            && user.canAnswer(message))
      {
         Form<AnswerForm> answerForm = MessageForm.AnswerForm();
         answerForm = answerForm.fill(new AnswerForm(idM));
         return ok(views.html.messages.createAnswer.render(null, null, duty, answerForm));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   @Dynamic("IsParticipantOfLecture")
   public static Result saveDutyAnswerCreate(long idL, long idA, long idD, long idM)
   {
      Message message = Message.findMessageByID(idM);
      Duty duty = message.getConversationDuty();
      Form<AnswerForm> answerForm = MessageForms.AnswerForm().bindFromRequest();
      User user = User.findBySession(session());
      if (duty != null && duty.getAssignment().getLecture().getId() == idL 
            && duty.getAssignment().getId() == idA && message != null 
            && user.canAnswer(message))
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
                  .render(null, null, duty, answerForm));
         }
         else
         {
            Message.createAnswer(answerForm.get().text, idM, user);
            return redirect(routes.AccountController.showMyMessages());
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
   
   @Dynamic("IsParticipantOfLecture")
   public static Result createAssignmentMessage(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         return ok(views.html.messages.createMessage.render(null, assignment, null, MessageForms.MessageForm()));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   @Dynamic("IsParticipantOfLecture")
   public static Result saveAssignmentMessageCreate(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      Form<MessageForm> messageForm = MessageForms.MessageForm().bindFromRequest();
      User user = User.findBySession(session());
      if (assignment != null && assignment.getLecture().getId() == idL)
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
                  .render(null, assignment, null, messageForm));
         }
         else
         {
            Message.createMessage(messageForm.get().text, null, assignment, null, user);
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   @Dynamic("IsParticipantOfLecture")
   public static Result createAssignmentAnswer(long idL, long idA, long idM)
   {
      Message message = Message.findMessageByID(idM);
      Assignment assignment = message.getConversationAssignment();
      User user = User.findBySession(session());
      if (assignment != null && assignment.getLecture().getId() == idL && message != null
            && user.canAnswer(message))
      {
         Form<AnswerForm> answerForm = MessageForm.AnswerForm();
         answerForm = answerForm.fill(new AnswerForm(idM));
         return ok(views.html.messages.createAnswer.render(null, assignment, null, answerForm));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   @Dynamic("IsParticipantOfLecture")
   public static Result saveAssignmentAnswerCreate(long idL, long idA, long idM)
   {
      Message message = Message.findMessageByID(idM);
      Assignment assignment = message.getConversationAssignment();
      Form<AnswerForm> answerForm = MessageForms.AnswerForm().bindFromRequest();
      User user = User.findBySession(session());
      if (assignment != null && assignment.getLecture().getId() == idL && message != null)
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
                  .render(null, assignment, null, answerForm));
         }
         else
         {
            Message.createAnswer(answerForm.get().text, idM, user);
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }
   
   @Dynamic("IsParticipantOfLecture")
   public static Result createLectureMessage(long idL)
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
   public static Result saveLectureMessageCreate(long idL)
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
   public static Result createLectureAnswer(long idL, long idM)
   {
      Message message = Message.findMessageByID(idM);
      Lecture lecture = message.getConversationLecture();
      User user = User.findBySession(session());
      if (lecture != null && message != null && lecture.getId() == idL && user.canAnswer(message))
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
   public static Result saveLectureAnswerCreate(long idL, long idM)
   {
      Message message = Message.findMessageByID(idM);
      Lecture lecture = message.getConversationLecture();
      Form<AnswerForm> answerForm = MessageForms.AnswerForm().bindFromRequest();
      User user = User.findBySession(session());
      if (lecture != null && message != null && lecture.getId() == idL && user.canAnswer(message))
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
