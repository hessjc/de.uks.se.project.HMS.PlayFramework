package controllers;

import models.Semester;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import security.HDSDynamicResourceHandler;
import utils.ErrorHelper;
import utils.ErrorHelper.ErrorHandler;
import be.objectify.deadbolt.java.actions.Dynamic;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import forms.SemesterForms;
import forms.SemesterForms.CreateSemesterForm;

@SubjectPresent
public class SemesterController extends Controller
{

   /**
    * render the semesters-view
    * 
    * @return semesters-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result showSemesters()
   {
      return ok(views.html.semester.semesters.render());
   }

   /**
    * create the semester and redirect to show semesters
    * 
    * @return create-semester-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result createSemester()
   {
      Form<CreateSemesterForm> form = SemesterForms.CreateSemesterForm().bindFromRequest();
      if (form.hasErrors())
      {
         ErrorHelper.handleErrors(form, new ErrorHandler() {
            @Override
            public void handleError(String identifier, ValidationError error)
            {
               flash(identifier, error.message());
            }
         });
         return badRequest(views.html.semester.semesters.render());
      }
      else
      {
         Semester semester = new Semester();
         semester.setSemester(form.get().semester);
         semester.save();
         flash("success", Messages.get("semester.create.success", semester.getSemester()));
         return redirect(routes.SemesterController.showSemesters());
      }
   }
}
