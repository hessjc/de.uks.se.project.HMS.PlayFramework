package controllers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import models.Assessment;
import models.Assignment;
import models.Duty;
import models.Lecture;
import models.Semester;
import models.User;
import models.Valuation;
import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import security.HDSDynamicResourceHandler;
import utils.ErrorHelper;
import utils.ErrorHelper.ErrorHandler;
import utils.MailUtil;
import utils.PlayHelper;
import utils.UserUtil;
import be.objectify.deadbolt.java.actions.Dynamic;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import forms.SimpleForms;
import forms.SimpleForms.IDForm;
import forms.ValuationForms;
import forms.ValuationForms.ValuationForm;

@SubjectPresent
public class AssessmentController extends Controller
{

   /**
    * render the show-assessment-view for the lecture with the given id
    * 
    * @param id
    * @return show-assessment-view
    */
   @Dynamic("IsLectureadminOfLecture")
   public static Result showAssessments(Long id)
   {
      return ok(views.html.assessment.showassessment.render(Lecture.findLectureByID(id)));
   }

   /**
    * close an assessment after all valuations are evaluated
    * 
    * @param id
    * @return show-assessment-view
    */
   @Dynamic("Proofreader")
   public static Result closeAssessment()
   {
      Form<IDForm> form = SimpleForms.IDForm().bindFromRequest();
      if (!form.hasErrors())
      {
         long assessmentID = form.get().id;
         Assessment assessment = Assessment.findAssessmentByID(assessmentID);
         if (assessment != null)
         {
            User proofreader = assessment.getProofreader();
            if (proofreader != null && proofreader.equals(User.findBySession(session()))
                  && Assessment.allValuationsCorrected(assessmentID))
            {
               Duty.closeDuty(assessment.getDuty());
               flash("assessmentclosed", "Die Bewertung (ID:" + assessment.getDuty().getId()
                     + ") wurde erfolgreich abgeschlossen.");

               //send mail to student
               final String subject = Messages.get("assessment.evaluated.email.subject",
                     assessment.getDuty().getAssignment().getName(), assessment.getDuty().getAssignment().getLecture()
                           .getSemester().getSemester(), assessment.getDuty().getAssignment().getLecture().getName());

               MailUtil.sendMail(ctx(), assessment.getDuty().getUser(), subject,
                     "views.%s.email.newEvaluatedAssessment_%s", assessment.getDuty().getUser(), assessment.getDuty()
                           .getAssignment().getLecture());
               return redirect(routes.AccountController.showMyDutys());
            }
            else
            {
               flash("assessmenterror",
                     "Es sind nicht alle Teilaufgaben bewertet, daher kann die Bewertung nicht abgeschlossen werden");
               return badRequest(views.html.assessment.evaluateassessment.render(
                     Assessment.findAssessmentByID(assessmentID)));
            }
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   /**
    * render the evaluate-assessment-view for the assessment with the given id
    * 
    * @param id
    * @return evaluate-assessment-view
    */
   @Dynamic("Proofreader")
   public static Result evaluateDuty(Long id)
   {
      Assessment assessment = Assessment.findAssessmentByID(id);
      if (assessment != null)
      {
         User proofreader = assessment.getProofreader();
         User user = User.findBySession(session());
         if (HDSDynamicResourceHandler.ADMINISTRATOR.equals(user.getRole().getName()))
         {
            return ok(views.html.assessment.evaluateassessment.render(assessment));
         }
         if (proofreader != null && proofreader.equals(user))
         {
            return ok(views.html.assessment.evaluateassessment.render(assessment));
         }
         Lecture lecture = assessment.duty.getAssignment().getLecture();
         if (user.isLectureadminOf().contains(lecture))
         {
            return ok(views.html.assessment.evaluateassessment.render(assessment));
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   @Dynamic("Proofreader")
   public static Result saveValuation(Long idA, Long idV)
   {
      Valuation valuation = Valuation.findValuationByID(idV);
      Assessment assessment = Assessment.findAssessmentByID(idA);

      if (valuation != null && assessment != null && assessment.getId() == valuation.getAssessment().getId())
      {
         User proofreader = assessment.getProofreader();
         User user = User.findBySession(session());

         boolean proofreaderEdit = proofreader != null && proofreader.equals(user)
               && !assessment.getDuty().isCorrected();
         boolean lectureAdminEdit = UserUtil.isLectureAdminOfLecture(assessment.getDuty().getAssignment().getLecture(),
               user);

         if (proofreaderEdit || lectureAdminEdit)
         {
            Form<ValuationForm> valuationForm = ValuationForms.ValuationForm().bindFromRequest();
            // update the valuation only if the duty is not corrected
            boolean hasErrors = valuationForm.hasErrors();
            //            valuation.setCorrected(!hasErrors);
            if (hasErrors)
            {
               ErrorHelper.handleErrors(valuationForm, new ErrorHandler() {
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
               Valuation.editValuation(valuation, valuationForm.get().resultpoints, valuationForm.get().comment, true,
                     valuation.getAssessment(), valuation.getSubtask());
            }
            if (PlayHelper.isPjaxRequest(request()))
            {
               return ok(views.html.valuations.showvaluation.render(valuation, valuationForm));
            }
            else
            {
               return ok(views.html.assessment.evaluateassessment.render(assessment));
            }
         }
      }
      else
      {
         try
         {
            Logger.debug(String
                  .format(
                        "Saving valuation failed. valuation: (%s), valuation.assessment: (%s), valuation.assessment.id(%s), assessment: (%s), assessment.id: (%s)",
                        valuation, valuation.assessment, valuation.assessment.id, assessment, assessment.id));
         }
         catch (Exception e)
         {
            Logger.debug("Saving valuation failed", e);
         }
      }
      return forbidden(views.html.errors.e403.render());
   }

   @Dynamic("IsLectureadminOfLecture")
   public static float overallPercentageAssessment(Long userid, Long lectureid)
   {
      List<Duty> dutys = Duty.findUploadedDutysByUserID(userid);
      int count = 0;
      float percentage = 0;
      for (Duty duty : dutys)
      {
         if (duty.getAssignment().getLecture().getId() == lectureid)
         {
            Assessment assessment = Assessment.findAssessmentByDutyID(duty.id);
            if (assessment != null && assessment.duty.corrected)
            {
               percentage = percentage
                     + (Assessment.percentageByDutyID(assessment.id) * assessment.duty.getAssignment()
                           .getMultiplicator());
               count++;
            }
         }
      }
      if (count == 0)
      {
         return 0;
      }
      else
      {
         return percentage / count;
      }
   }

   @Dynamic("IsLectureadminOfLecture")
   public static int noConsideredAssessments(Long userid, Long lectureid)
   {
      List<Duty> dutys = Duty.findUploadedDutysByUserID(userid);
      int count = 0;
      for (Duty duty : dutys)
      {
         if (duty.getAssignment().getLecture().getId() == lectureid)
         {
            count++;
         }
      }
      return Assignment.findAssignmentsByLectureID(lectureid).size() - count;
   }

   @Dynamic("Proofreader")
   public static List<Assessment> myEvaluatedAssessments()
   {
      return Assessment.findEvaluatedAssessmentsByUserID(User.findBySession(session()).getId());
   }

   @Dynamic("Proofreader")
   public static Map<Semester, Map<Lecture, Collection<Assessment>>> myEvaluatedAssessmentsPerSemester()
   {
      SortedMap<Semester, Map<Lecture, Collection<Assessment>>> assessmentMap = new TreeMap<Semester, Map<Lecture, Collection<Assessment>>>();
      for (Assessment assessment : Assessment.findEvaluatedAssessmentsByUserID(User.findBySession(session()).getId()))
      {
         Lecture lecture = assessment.duty.getAssignment().getLecture();
         Semester semester = lecture.getSemester();
         Map<Lecture, Collection<Assessment>> assessmentsPerLecture = assessmentMap.get(semester);
         if (assessmentsPerLecture == null)
         {
            assessmentsPerLecture = new TreeMap<Lecture, Collection<Assessment>>();
            assessmentMap.put(semester, assessmentsPerLecture);
         }
         Collection<Assessment> assessments = assessmentsPerLecture.get(lecture);
         if (assessments == null)
         {
            assessments = new LinkedList<Assessment>();
            assessmentsPerLecture.put(lecture, assessments);
         }
         assessments.add(assessment);
      }

      return assessmentMap;
   }
}
