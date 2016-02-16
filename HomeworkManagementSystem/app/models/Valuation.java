package models;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "valuations")
@Table(name = "valuations")
public class Valuation extends HMSModelElement
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   public float resultpoints;

   public String comment;

   public boolean corrected = false;

   @ManyToOne
   public Assessment assessment;

   @ManyToOne
   public Subtask subtask;

   public static final Finder<Long, Valuation> find = new Finder<Long, Valuation>(
         Long.class, Valuation.class);

   public static List<Valuation> findAll()
   {
      return find.all();
   }

   public static Valuation findValuationByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }

   public static Valuation createValuation(Assessment assessment, Subtask subtask)
   {
      Valuation valuation = new Valuation();
      valuation.save();
      valuation.setResultpoints(-1);
      valuation.setComment("");
      valuation.update();
      valuation.setAssessment(assessment);
      valuation.setSubtask(subtask);
      return valuation;
   }

   public static void editValuation(Valuation valuation, float resultpoints, String comment, boolean corrected,
         Assessment assessment, Subtask subtask)
   {
      valuation.setResultpoints(resultpoints);
      valuation.setComment(comment);
      valuation.setCorrected(corrected);
      valuation.update();
      valuation.setAssessment(assessment);
      valuation.setSubtask(subtask);
   }

   public static void deleteValuation(Valuation valuation)
   {
      valuation.setAssessment(null);
      valuation.setSubtask(null);
      valuation.delete();
   }

   public static Set<Valuation> createValuations(Assessment assessment)
   {
      Set<Valuation> valuations = new LinkedHashSet<Valuation>();

      for (Subtask subtask : assessment.getDuty().getAssignment().getSubtasks())
      {
         Valuation valuation = Valuation.createValuation(assessment, subtask);
         valuations.add(valuation);
      }
      return valuations;
   }

   public static List<Valuation> findEvaluatedAssessmentsByUserID(long id)
   {
      return find.where().eq("proofreader_id", id).findList();
   }

   public static float resultpointsByUserANDDutyIDANDSubtaskID(long idU, long idD, long idS)
   {
      Subtask subtask = Subtask.findSubtaskByID(idS);
      Duty duty = Duty.findDutyByID(idD);
      if (subtask == null || duty == null || duty.getAssessment() == null)
      {
         return -1;
      }
      else
      {
         Valuation valuation = Valuation.findValuationByAssessmentANDSubtaskID(duty.getAssessment().getId(), subtask.getId());
         if (valuation != null)
         {
            return valuation.getResultpoints();
         }
         else
         {
            return -1;
         }
      }
   }

   public static float resultpointsByUserANDSubtaskID(long idU, long idS)
   {
      Duty duty = Duty
            .findDutyByUserANDAssignment(User.findUserByID(idU), Subtask.findSubtaskByID(idS).getAssignment());
      if (duty == null) return -1;
      else return Valuation.findValuationByAssessmentANDSubtaskID(duty.getAssessment().getId(), Subtask.findSubtaskByID(idS).getId()).getResultpoints();
   }

   private static Valuation findValuationByAssessmentANDSubtaskID(long idA, long idS)
   {
      return find.where().conjunction().eq("assessment_id", idA).eq("subtask_id", idS).findUnique();
   }

   public static List<Valuation> findValuationsByAssessmentID(long id)
   {
      return find.where().eq("assessment_id", id).findList();
   }

   public long getId()
   {
      return id;
   }

   public float getResultpoints()
   {
      return resultpoints;
   }

   public void setResultpoints(float resultpoints)
   {
      this.resultpoints = resultpoints;
   }

   public String getComment()
   {
      return comment;
   }

   public void setComment(String comment)
   {
      this.comment = comment;
   }

   public boolean isCorrected()
   {
      return corrected;
   }

   public void setCorrected(boolean corrected)
   {
      this.corrected = corrected;
   }

   public Assessment getAssessment()
   {
      return assessment;
   }

   public void setAssessment(Assessment assessment)
   {
      if (this.assessment != assessment)
      {
         Assessment oldAssessment = this.assessment;
         this.assessment = assessment;
         if (oldAssessment != null)
         {
            oldAssessment.removeValuation(this);
         }
         if (assessment != null)
         {
            assessment.addValuation(this);
         }
         this.update();
      }
   }

   public Subtask getSubtask()
   {
      return subtask;
   }

   public void setSubtask(Subtask subtask)
   {
      if (this.subtask != subtask)
      {
         Subtask oldSubtask = this.subtask;
         this.subtask = subtask;
         if (oldSubtask != null)
         {
            oldSubtask.removeValuation(this);
         }
         if (subtask != null)
         {
            subtask.addValuation(this);
         }
         this.update();
      }
   }
}
