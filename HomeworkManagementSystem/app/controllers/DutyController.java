package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Assessment;
import models.Assignment;
import models.Duty;
import models.Lecture;
import models.User;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import security.HDSDynamicResourceHandler;
import utils.DateHelper;
import utils.FileHelper;
import utils.StringHelper;
import be.objectify.deadbolt.java.actions.Dynamic;
import forms.AssignmentForms;
import forms.AssignmentForms.ManualUploadForm;

/**
 * Homework Management System - Important Duty User Information
 * Duty class for students dutys
 * 
 * @version 1.0
 */
@Dynamic("Proofreader")
public class DutyController extends Controller
{

   /**
    * upload the duty
    * 
    * @param idL
    * @param idA
    * @return redirect AssignmentController.showDuty()
    */
   @BodyParser.Of(value = BodyParser.MultipartFormData.class, maxLength = 50 * 1000 * 1024)
   @Dynamic("IsParticipantOfLecture")
   public static Result uploadDuty(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         if (DateHelper.isExpired(assignment.getDeadline()))
         {
            flash("error", Messages.get("Abgabefrist bereits abgelaufen"));
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }

         if (request().body().isMaxSizeExceeded())
         {
            flash("error", Messages.get("duty.upload.toobig"));
            return badRequest(views.html.assignment.showAssignment.render(assignment));
         }
         MultipartFormData body = request().body().asMultipartFormData();
         FilePart filePart = body.getFile("duty");
         User user = User.findBySession(session());

         flash().remove("error");

         if (FileHelper.upload(user, assignment, body, filePart))
         {
            flash("success", Messages.get("duty.upload.successful"));
         }
         else
         {
            // If now specific failure was set, add a general one
            if (!flash().containsKey("error"))
            {
               flash("error", Messages.get("duty.upload.failure"));
            }
         }
         return redirect(routes.AssignmentController.showAssignment(idL, idA));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * upload duty as administrator
    * 
    * @param idL
    * @param idA
    * @return redirect AssignmentController.showDuty()
    */
   @Dynamic("IsProofreaderOfLecture")
   public static Result uploadDutyFromAdmin(long idL, long idA)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      if (assignment != null && assignment.getLecture().getId() == idL)
      {
         Form<ManualUploadForm> uploadForm = AssignmentForms.ManualUploadForm().bindFromRequest();
         MultipartFormData body = request().body().asMultipartFormData();
         FilePart filePart = body.getFile("dutyFromAdmin");
         Matcher matcher = Pattern.compile("(\\d+).*").matcher(uploadForm.get().user);
         if (matcher.find())
         {
            User user = User.findUser(matcher.group(1));
            if (FileHelper.upload(user, assignment, body, filePart))
            {
               flash("success", Messages.get("duty.upload.successful"));
            }
            else
            {
               // If now specific failure was set, add a general one
               if (!flash().containsKey("error"))
               {
                  flash("error", Messages.get("duty.upload.failure"));
               }
            }
            return redirect(routes.AssignmentController.showAssignment(idL, idA)); // to do
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   /**
    * return the duty with id idD
    * 
    * @param idL
    * @param idD
    * @return File
    */
   @Dynamic("IsParticipantOfLecture")
   public static Result downloadDuty(long idL, long idD)
   {
      Duty duty = Duty.findDutyByID(idD);
      Lecture lecture = duty.getAssignment().getLecture();
      if (duty != null && lecture.getId() == idL)
      {
         User dutyOwner = duty.user;
         User user = User.findBySession(session());
         if (user.equals(dutyOwner) || HDSDynamicResourceHandler.ADMINISTRATOR.equals(user.getRole().getName())
               || lecture.getProofreaders().contains(user)
               || lecture.getLectureadmins().contains(user))
         {
            String fileEnding = duty.uploadedFile.substring(duty.uploadedFile.lastIndexOf("."));
            File file = FileHelper.getDutyFile(dutyOwner, duty.getAssignment(), fileEnding);
            try
            {
               String path = file.getPath();
               response().setHeader("Content-Disposition",
                     "attachment; filename=" + path.substring(path.lastIndexOf(File.separator) + 1));
               return ok(new FileInputStream(file));
            }
            catch (FileNotFoundException e)
            {
               Logger.error(String.format("Datei %s wurde nicht gefunden.", file));
               return badRequest("Die Datei konnte nicht gefunden werden.");
            }
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   /**
    * catch specific duty by id and mark as resolved
    * 
    * @return dutys-view
    */
   @Dynamic("IsProofreaderOfLecture")
   public static Result catchDuty(Long idL, Long idA, Long idD)
   {
      Duty duty = Duty.findDutyByID(idD);
      if (duty != null && duty.getAssignment().getId() == idA && duty.getAssignment().getLecture().getId() == idL)
      {
         if (!duty.catched)
         {
            Assignment assignment = duty.getAssignment();
            if (assignment.getDeadline() == null || assignment.getDeadline().isBeforeNow())
            {
               Duty.catchDuty(duty, User.findBySession(session()));
               flash("success", Messages.get("duty.catch.successful", duty.id));
            }
            else
            {
               flash("error", Messages.get("duty.catch.deadlinenotover", duty.id));
            }
            return redirect(routes.AssignmentController.showAssignment(idL, idA));
         }
         else
         {
            flash("error", "Die Abgabe wird bereits von einem User bearbeitet!");
            return badRequest(views.html.assignment.showAssignment.render(Assignment.findAssignmentByID(idA)));
         }
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   @Dynamic("IsLectureadminOfLecture")
   public static Result releaseDuty(Long idL, Long idD)
   {
      Duty duty = Duty.findDutyByID(idD);
      if (duty != null && duty.getAssignment().getLecture().getId() == idL)
      {
         Duty.releaseDuty(duty);
         flash("released", "Die Abgabe (ID: " + duty.getId() + ") wurde zurückgelegt!");
         return redirect(routes.DutyController.showCatchedDutys(duty.getAssignment().getLecture().getId()));
      }
      else
      {
         return forbidden(views.html.errors.e403.render());
      }
   }

   /**
    * show all catched dutys by lecture id
    * 
    * @return cactheddutys-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result showCatchedDutys(Long idL)
   {
      return ok(views.html.lecture.duty.showCatchedDutys.render(Lecture.findLectureByID(idL)));
   }

   /**
    * show allocateable dutys
    * 
    * @return allocateable-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result showDutyAllocation(Long idL)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      return ok(views.html.duty.showDutyAllocation.render(lecture));
   }

   public static Result allocate1(Long idL, String dutys, String proofreaders)
   {
      Lecture lecture = Lecture.findLectureByID(idL);

      if (selectedDutys(dutys) && selectedProofreader(proofreaders))
      {
         String[] dutysArray = StringHelper.splitStringWithWhitespaces(dutys);
         String[] proofreadersArray = StringHelper.splitStringWithWhitespaces(proofreaders);
         for (int i = 0, j = 0; i < dutysArray.length; i++)
         {
            Duty duty = Duty.findDutyByID(Long.valueOf(dutysArray[i]).longValue());
            User proofreader = User.findUserByID(Long.valueOf(proofreadersArray[j++]).longValue());
            Duty.catchDuty(duty, proofreader);
            if (j == proofreadersArray.length) j = 0;
         }
         flash("success", Messages.get("dutys.catch.successful", dutys, proofreaders));
         return ok(views.html.duty.allocation.render(lecture));
      }
      else
      {
         flash("error", Messages.get("duty.catch.error"));
         return badRequest(views.html.message.alert.render());
      }
   }

   public static Result release1(Long idL, String dutys, String proofreaders)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      String[] dutysArray = StringHelper.splitStringWithWhitespaces(dutys);
      String[] proofreadersArray = StringHelper.splitStringWithWhitespaces(proofreaders);

      if (selectedProofreader(proofreaders))
      {
         if (selectedDutys(dutys))
         {
            for (int i = 0; i < dutysArray.length; i++)
            {
               Duty.releaseDuty(Duty.findDutyByID(Long.valueOf(dutysArray[i]).longValue()));
            }
         }
         else
         {
            for (int i = 0; i < proofreadersArray.length; i++)
            {
               User proofreader = User.findUserByID(Long.valueOf(proofreadersArray[i]).longValue());
               for (Assessment assessment : User.findUsersAssessmentsByLecture(proofreader.getId(), idL))
               {
                  Duty.releaseDuty(assessment.getDuty());
               }

            }
         }
         flash("success", Messages.get("duty.release.success", dutys, proofreaders));
         return ok(views.html.duty.allocation.render(lecture));
      }
      else
      {
         flash("error", Messages.get("duty.release.error"));
         return badRequest(views.html.message.alert.render());
      }

   }

   public static Result refreshCatchedDutys1(Long idL, String proofreaders)
   {
      String[] proofreadersArray = StringHelper.splitStringWithWhitespaces(proofreaders);
      Set<Duty> tmpDutys = new HashSet<Duty>();
      for (int i = 0; i < proofreadersArray.length; i++)
      {
         User proofreader = User.findUserByID(Long.valueOf(proofreadersArray[i]).longValue());
         for (Assessment assessment : proofreader.getAssessments())
         {
            if (assessment.getDuty().isCatched()) tmpDutys.add(assessment.getDuty());
         }
      }
      List<Duty> dutys = new LinkedList<Duty>(tmpDutys);
      Collections.sort(dutys, utils.ComparatorHelper.dutyComparator);
      return ok(views.html.form.multiselectable.dutys.render(dutys, "catchedDutys", "filterCatchedDutys",
            "btnCatchedDutys"));
   }

   public static Result allocate(Long idL, Long idA, String dutys, String proofreaders)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      Assignment assignment = Assignment.findAssignmentByID(idA);

      if (selectedDutys(dutys) && selectedProofreader(proofreaders))
      {
         String[] dutysArray = StringHelper.splitStringWithWhitespaces(dutys);
         String[] proofreadersArray = StringHelper.splitStringWithWhitespaces(proofreaders);
         for (int i = 0, j = 0; i < dutysArray.length; i++)
         {
            Duty duty = Duty.findDutyByID(Long.valueOf(dutysArray[i]).longValue());
            User proofreader = User.findUserByID(Long.valueOf(proofreadersArray[j++]).longValue());
            Duty.catchDuty(duty, proofreader);
            if (j == proofreadersArray.length) j = 0;
         }
         flash("success", Messages.get("dutys.catch.successful", dutys, proofreaders));
         return ok(views.html.assignment.allocation.render(lecture, assignment));
      }
      else
      {
         flash("error", Messages.get("duty.catch.error"));
         return badRequest(views.html.message.alert.render());
      }
   }

   public static Result release(Long idL, Long idA, String dutys, String proofreaders)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      Assignment assignment = Assignment.findAssignmentByID(idA);
      String[] dutysArray = StringHelper.splitStringWithWhitespaces(dutys);
      String[] proofreadersArray = StringHelper.splitStringWithWhitespaces(proofreaders);

      if (selectedProofreader(proofreaders))
      {
         if (selectedDutys(dutys))
         {
            for (int i = 0; i < dutysArray.length; i++)
            {
               Duty.releaseDuty(Duty.findDutyByID(Long.valueOf(dutysArray[i]).longValue()));
            }
         }
         else
         {
            for (int i = 0; i < proofreadersArray.length; i++)
            {
               User proofreader = User.findUserByID(Long.valueOf(proofreadersArray[i]).longValue());
               for (Assessment assessment : User.findUsersAssessmentsByLecture(proofreader.getId(), idL))
               {
                  Duty.releaseDuty(assessment.getDuty());
               }

            }
         }
         flash("success", Messages.get("duty.release.success", dutys, proofreaders));
         return ok(views.html.assignment.allocation.render(lecture, assignment));
      }
      else
      {
         flash("error", Messages.get("duty.release.error"));
         return badRequest(views.html.message.alert.render());
      }

   }

   public static Result refreshCatchedDutys(Long idL, Long idA, String proofreaders)
   {
      Assignment assignment = Assignment.findAssignmentByID(idA);
      String[] proofreadersArray = StringHelper.splitStringWithWhitespaces(proofreaders);
      Set<Duty> tmpDutys = new HashSet<Duty>();
      for (int i = 0; i < proofreadersArray.length; i++)
      {
         User proofreader = User.findUserByID(Long.valueOf(proofreadersArray[i]).longValue());
         for (Assessment assessment : proofreader.getAssessments())
         {
            if (assessment.getDuty().getAssignment().equals(assignment)) tmpDutys.add(assessment.getDuty());
         }
      }
      List<Duty> dutys = new LinkedList<Duty>(tmpDutys);
      Collections.sort(dutys, utils.ComparatorHelper.dutyComparator);
      return ok(views.html.form.multiselectable.dutys.render(dutys, "catchedDutys", "filterCatchedDutys",
            "btnCatchedDutys"));
   }

   public static boolean selectedDutys(String dutys)
   {
      return dutys.length() > 0 ? true : false;
   }

   public static boolean selectedProofreader(String proofreaders)
   {
      return proofreaders.length() > 0 ? true : false;
   }
}
