package controllers;

import models.Assignment;
import models.Subtask;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import utils.ErrorHelper;
import utils.ErrorHelper.ErrorHandler;
import be.objectify.deadbolt.java.actions.Dynamic;
import forms.AssignmentForms;
import forms.AssignmentForms.SubtaskForm;
import forms.SimpleForms;
import forms.SimpleForms.IDForm;

public class SubtaskController extends Controller implements Constants
{
   /*************************************************************************************************************************
    ***************************************************** Subtask*************************************************************
    *************************************************************************************************************************/

   /**
    * render the create-subtask-view for the lecture with the id idL
    * and the assignment with the id idA
    * 
    * @param idL
    * @param idA
    * @return create-subtask-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result createSubtask(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment.isCompleted())
      {
         flash("error", ASSIGNMENT_COMPLETED);
         return redirect(routes.AssignmentController.showAssignment(idL, idA));
      }

      Form<SubtaskForm> subtaskForm = SubtaskForm.SubtaskForm();
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         subtaskForm = subtaskForm.fill(new SubtaskForm(assignment));
         return ok(views.html.assignment.subtask.createSubtasks.render(subtaskForm, false));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * save a correct created subtask or handle possible mistakes
    * 
    * @param idL
    * @param idA
    * @return create-subtask-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result saveSubtaskCreate(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      Form<SubtaskForm> subForm = AssignmentForms.SubtaskForm().bindFromRequest();
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         if (subForm.hasErrors())
         {
            ErrorHelper.handleErrors(subForm, new ErrorHandler() {
               @Override
               public void handleError(String identifier, ValidationError error)
               {
                  flash(identifier, error.message());
               }
            });
            subForm = subForm.fill(new SubtaskForm(assignment));
            return badRequest(views.html.assignment.subtask.createSubtasks
                  .render(subForm, false));
         }
         else
         {
            Subtask.createSubtask(subForm.get().name, subForm.get().description,
                  Float.parseFloat(subForm.get().points), subForm.get().additional, assignment);
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * delete the subtask with the id idS
    * 
    * @param idL
    * @param idA
    * @param idS
    * @return create-subtask-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result deleteSubtask(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment.isCompleted())
      {
         flash("error", ASSIGNMENT_COMPLETED);
         return redirect(routes.AssignmentController.showAssignment(idL, idA));
      }

      Form<IDForm> form = SimpleForms.IDForm().bindFromRequest();
      if (!form.hasErrors())
      {
         Subtask subtask = Subtask.findSubtaskByID(form.get().id);
         if (subtask != null && subtask.assignment.getId() == idA && subtask.assignment.getLecture().getId() == idL)
         {
            Subtask.deleteSubtask(subtask);
            flash("success", Messages.get("assignment.subtask.delete.success"));
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   /**
    * render the create-subtask-view with filled fields for the
    * subtask with the id idS
    * and the assignment with the id idA
    * 
    * @param idL
    * @param idA
    * @param idS
    * @return create-subtask-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result editSubtask(long idL, long idA, long idS)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment.isCompleted())
      {
         flash("error", ASSIGNMENT_COMPLETED);
         return redirect(routes.AssignmentController.showAssignment(idL, idA));
      }

      Subtask subtask = Subtask.findSubtaskByID(idS);
      if (subtask != null && subtask.assignment.getId() == idA && subtask.assignment.getLecture().getId() == idL)
      {
         Form<SubtaskForm> form = SubtaskForm.SubtaskForm();
         form = form.fill(new SubtaskForm(subtask.id, subtask.name, subtask.points + "", subtask.description,
               subtask.additional, assignment));
         return ok(views.html.assignment.subtask.createSubtasks.render(form, true));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * save a correct edited subtask with the id idS or handle possible mistakes
    * 
    * @param idL
    * @param idA
    * @param idS
    * @return create-subtask-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result saveSubtaskEdit(long idL, long idA, long idS)
   {
      Subtask subtask = Subtask.findSubtaskByID(idS);
      if (subtask != null && subtask.assignment.getId() == idA && subtask.assignment.getLecture().getId() == idL)
      {
         Assignment assignment = subtask.assignment;
         Form<SubtaskForm> subForm = AssignmentForms.SubtaskForm().bindFromRequest();
         if (subForm.hasErrors())
         {
            ErrorHelper.handleErrors(subForm, new ErrorHandler() {
               @Override
               public void handleError(String identifier, ValidationError error)
               {
                  flash(identifier, error.message());
               }
            });
            subForm = subForm.fill(new SubtaskForm(subtask.id, subtask.name, subtask.points + "", subtask.description,
                  subtask.additional, assignment));
            return badRequest(views.html.assignment.subtask.createSubtasks.render(subForm, true));
         }
         else
         {
            Subtask.editSubtask(subtask, subForm.get().name, subForm.get().description,
                  Float.parseFloat(subForm.get().points), subForm.get().additional, assignment);
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   public static Result openSubtasks(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (areSubtasksEditable(assignment))
      {
         assignment.setCompleted(false);
         flash("success", Messages.get("assignment.aresubtaskeditable.true"));
      }
      else flash("error", Messages.get("assignment.aresubtaskeditable.false"));
      return ok(views.html.assignment.showAssignment.render(assignment));
   }

   public static boolean areSubtasksEditable(Assignment assignment)
   {
      return assignment.getDutys().size() == 0 ? true : false;
   }
}
