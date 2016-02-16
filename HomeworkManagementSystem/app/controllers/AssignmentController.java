package controllers;

import java.io.File;

import models.Assignment;
import models.Lecture;
import models.Message;
import models.Stuff;
import models.User;
import play.Play;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.Constants;
import utils.ErrorHelper;
import utils.ErrorHelper.ErrorHandler;
import utils.FileHelper;
import utils.UserUtil;
import be.objectify.deadbolt.java.actions.Dynamic;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import forms.AssignmentForms;
import forms.MessageForms.AnswerForm;
import forms.AssignmentForms.AssignmentForm;
import forms.MessageForms;
import forms.MessageForms.MessageForm;
import forms.AssignmentForms.StuffForm;

@SubjectPresent
public class AssignmentController extends Controller implements Constants
{
   /*************************************************************************************************************************
    ***************************************************** Assignment**********************************************************
    *************************************************************************************************************************/

   /**
    * render the show-assignment-view for the assignment with the id idA
    * and the lecture with the id idL
    * 
    * @param idL
    * @param idA
    * @return show-assignment-view
    */
   @Dynamic("IsParticipantOfLecture")
   public static Result showAssignment(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         if (assignment.isCompleted()
               || (!assignment.isCompleted() && UserUtil.isAllowed(Http.Context.current(), "IsLectureadminOfLecture")))
         {
            return ok(views.html.assignment.showAssignment.render(assignment));
         }
      }

      return forbidden(views.html.errors.e403.render());
   }

   /**
    * render the create-assignment-view for the lecture with the id idL
    * 
    * @param idL
    * @return create-assignment-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result createAssignment(long idL)
   {
      Form<AssignmentForm> assignmentForm = AssignmentForms.AssignmentForm();
      assignmentForm = assignmentForm.fill(new AssignmentForm());
      return ok(views.html.assignment.createAssignment.render(assignmentForm, idL));
   }

   /**
    * Completes the given assignment
    * 
    * @param idL
    * @return create-assignment-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result completeAssignment(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment.isCompleted())
      {
         flash("error", "Die Aufgabe ist bereits abgeschlossen!");
      }
      else
      {
         assignment.setCompleted(true);
         assignment.update();
      }
      return redirect(routes.AssignmentController.showAssignment(idL, idA));
   }

   /**
    * save a correct created assignment or handle possible mistakes
    * 
    * @param idL
    * @return create-assignment-view (badRequest)
    * @return show-assignment-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result saveAssignmentCreate(long idL)
   {
      Form<AssignmentForm> assignmentForm = AssignmentForms.AssignmentForm().bindFromRequest();
      if (assignmentForm.hasErrors())
      {
         ErrorHelper.handleErrors(assignmentForm, new ErrorHandler() {
            @Override
            public void handleError(String identifier, ValidationError error)
            {
               flash(identifier, error.message());
            }
         });
         return badRequest(views.html.assignment.createAssignment.render(assignmentForm, idL));
      }
      else
      {
         Lecture lecture = Lecture.findLectureByID(idL);
         Assignment assignment = Assignment.createAssignment(
               assignmentForm.get().name,
               assignmentForm.get().description,
               assignmentForm.get().deadline,
               assignmentForm.get().multiplicator,
               assignmentForm.get().comment,
               assignmentForm.get().additional,
               lecture);
         return redirect(routes.AssignmentController.showAssignment(idL, assignment.getId()));
      }
   }

   /**
    * render the edit-assignment-view for the lecture with the id idL
    * and the assignment with the id idA
    * 
    * @param idL
    * @param idA
    * @return create-assignment-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result editAssignment(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         if (assignment.isCompleted())
         {
            flash("error", ASSIGNMENT_COMPLETED);
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }
         else
         {
            Form<AssignmentForm> assignmentForm = AssignmentForms.AssignmentForm();
            assignmentForm = assignmentForm.fill(new AssignmentForm(assignment));
            return ok(views.html.assignment.editAssignment.render(assignmentForm,
                  AssignmentForms.StuffForm(), idL));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * save a correct edited assignment or handle possible mistakes
    * 
    * @param idL
    * @param idA
    * @return create-assignment-view (badRequest)
    * @return show-assignment-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result saveAssignmentEdit(long idL, long idA)
   {
      Form<AssignmentForm> assignmentForm = AssignmentForms.AssignmentForm().bindFromRequest();
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         if (assignment.isCompleted() || assignmentForm.hasErrors())
         {
            if (assignment.isCompleted())
            {
               flash("error", ASSIGNMENT_COMPLETED);
            }
            else
            {
               ErrorHelper.handleErrors(assignmentForm, new ErrorHandler() {
                  @Override
                  public void handleError(String identifier, ValidationError error)
                  {
                     flash(identifier, error.message());
                  }
               });
            }
            assignmentForm = assignmentForm.fill(new AssignmentForm(assignment));
            return badRequest(views.html.assignment.editAssignment.render(assignmentForm,
                  AssignmentForms.StuffForm(), idL));
         }
         else
         {
            Assignment.editAssignment(assignment,
                  assignmentForm.get().name,
                  assignmentForm.get().description,
                  assignmentForm.get().deadline,
                  assignmentForm.get().multiplicator,
                  assignmentForm.get().comment,
                  assignmentForm.get().additional);
            return redirect(routes.AssignmentController.showAssignment(idL, assignment.getId())); // to do
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /*************************************************************************************************************************
    ***************************************************** Stuff***************************************************************
    *************************************************************************************************************************/

   /**
    * return the stuff with the id idSt
    * 
    * @param idL
    * @param idA
    * @param idSt
    * @return File
    */
   @Dynamic("IsParticipantOfLecture")
   public static Result downloadStuff(long idL, long idA, long idSt)
   {
      Stuff stuff = Stuff.findStuffByID(idSt);
      if (stuff != null && stuff.assignment.getLecture().getId() == idL && stuff.assignment.getId() == idA)
      {
         return ok(getStuffFile(stuff.assignment, stuff.fileName, stuff.type));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * delete the Stuff with the id idSt
    * 
    * @param idL
    * @param idA
    * @param idSt
    * @return edit-assignment-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result deleteStuff(long idL, long idA, long idSt)
   {
      Stuff stuff = Stuff.findStuffByID(idSt);
      if (stuff != null && stuff.assignment.getLecture().getId() == idL && stuff.assignment.getId() == idA)
      {
         Assignment assignment = stuff.assignment;
         if (assignment.isCompleted())
         {
            flash("error", ASSIGNMENT_COMPLETED);
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }
         else
         {
            Form<AssignmentForm> assignmentForm = AssignmentForms.AssignmentForm();
            assignmentForm = assignmentForm.fill(new AssignmentForm(assignment));
            File file = new File(Play.application().path() + "");
            file.delete();
            stuff.delete();
            return redirect(routes.AssignmentController.editAssignment(idL, idA));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * save the correct uploaded stuff or handle possible mistakes
    * 
    * @param idL
    * @param idA
    * @return edit-assignment-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result saveStuff(long idL, long idA)
   {
      MultipartFormData body = request().body().asMultipartFormData();
      Form<StuffForm> stuffForm = AssignmentForms.StuffForm().bindFromRequest();
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         if (assignment.isCompleted() || stuffForm.hasErrors())
         {
            if (assignment.isCompleted())
            {
               flash("error", ASSIGNMENT_COMPLETED);
            }
            else
            {
               ErrorHelper.handleErrors(stuffForm, new ErrorHandler() {
                  @Override
                  public void handleError(String identifier, ValidationError error)
                  {
                     flash(identifier, error.message());
                  }
               });
            }
            Form<AssignmentForm> assignmentForm = AssignmentForms.AssignmentForm();
            assignmentForm = assignmentForm.fill(new AssignmentForm(assignment));
            return badRequest(views.html.assignment.editAssignment.render(assignmentForm,
                  AssignmentForms.StuffForm(), idL));
         }

         FilePart filePart = body.getFile("stuff");
         if (filePart != null)
         {
            int pointIndex = filePart.getFilename().lastIndexOf(".");
            String type = filePart.getFilename().substring(pointIndex + 1);
            String fileName = stuffForm.get().stuffName;
            File file = filePart.getFile();

            File destFile = getStuffFile(assignment, fileName, type);
            FileHelper.copyFile(file, destFile);
            Stuff.createStuff(fileName, type, assignment);
         }
         return redirect(routes.AssignmentController.editAssignment(idL, assignment.getId())); // to do
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /*************************************************************************************************************************
    ***************************************************** Duty****************************************************************
    *************************************************************************************************************************/

   public static File getStuffFile(final Assignment assignment, final String fileName, final String type)
   {
      String semester = assignment.getLecture().getSemester().getSemester();
      String lectureName = assignment.getLecture().getName();

      String path = (getStuffPath(semester, lectureName, assignment.getName()) + fileName + "." + type).replaceAll(
            " ", "_");
      return new File(Play.application().path() + path);
   }

   @Dynamic("IsParticipantOfLecture")
   private static String getStuffPath(String semester, String lectureName, String assignmentName)
   {
      String semesterparts[] = semester.split("/");
      if (semesterparts.length > 1)
      {
         semester = semesterparts[0] + "_" + semesterparts[1];
      }
      String path = (UPLOAD_BASE_FOLDER + semester + "/" + lectureName + "/"
            + assignmentName + "/Material").replaceAll(" ", "_");
      File dirs = new File(Play.application().path() + path);
      dirs.mkdirs();
      path = path + "/";
      return path;
   }

}
