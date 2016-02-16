package models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.i18n.Messages;

@Entity(name = "assessments")
@Table(name = "assessments")
public class Assessment extends HMSModelElement
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @OneToMany(mappedBy = "assessment")
   public Set<Valuation> valuations = new LinkedHashSet<Valuation>();

   @OneToOne
   public Duty duty;

   @ManyToOne
   public User proofreader;

   public static final Finder<Long, Assessment> find = new Finder<Long, Assessment>(
         Long.class, Assessment.class);

   public static List<Assessment> findAll()
   {
      return find.all();
   }

   public static Assessment findAssessmentByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }

   public static Assessment findAssessmentByDutyID(long id)
   {
      return find.where().eq("duty_id", id).findUnique();
   }

   public static Assessment createAssessment(Duty duty)
   {
      Assessment assessment = new Assessment();
      assessment.save();
      assessment.setDuty(duty);
      assessment.setValuations(Valuation.createValuations(assessment));
      return assessment;
   }

   public static void deleteAssessment(Assessment assessment)
   {
      ArrayList<Valuation> valuationList = new ArrayList<Valuation>();
      valuationList.addAll(assessment.getValuations());
      for (Valuation valuation : valuationList)
      {
         Valuation.deleteValuation(valuation);
      }
      assessment.setDuty(null);
      assessment.setProofreader(null);
   }

   public static float pointsByID(long id)
   {
      Assessment assessment = Assessment.findAssessmentByID(id);
      return Assignment.pointsByAssignmentID(assessment.getDuty().getAssignment().getId());
   }

   public static float pointsByDutyID(long id)
   {
      Assessment assessment = Assessment.findAssessmentByDutyID(id);
      return pointsByDutyID(assessment);
   }

   public static float pointsByDutyID(Assessment assessment)
   {
      return Assignment.pointsByAssignmentID(assessment.getDuty().getAssignment().getId());
   }

   public static float resultpointsByID(long id)
   {
      Assessment assessment = Assessment.findAssessmentByID(id);
      if (assessment.getValuations().size() == 0) return -1;
      else
      {
         float result = 0;
         for (Valuation valuation : assessment.getValuations())
         {
            result = result + valuation.getResultpoints();
         }
         return result;
      }
   }

   public static float resultpointsByDutyID(long id)
   {
      Assessment assessment = Assessment.findAssessmentByDutyID(id);
      return resultpointsByDutyID(assessment);
   }

   public static float resultpointsByDutyID(Assessment assessment)
   {
      if (assessment.getValuations().size() == 0) return -1;
      else
      {
         float result = 0;
         for (Valuation valuation : assessment.getValuations())
         {
            result = result + valuation.getResultpoints();
         }
         return result;
      }
   }

   public static float percentageByID(long id)
   {
      return (Math.round(resultpointsByID(id) / pointsByID(id) * 10000)) / 100f;
   }

   public static float percentageByDutyID(long id)
   {
      return (Math.round(resultpointsByDutyID(id) / pointsByDutyID(id) * 10000)) / 100f;
   }

   public static String percentageByDutyIDAsString(long id)
   {
      Assessment assessment = Assessment.findAssessmentByDutyID(id);
      if (assessment.getValuations().isEmpty() || !assessment.getDuty().isCorrected())
      {
         return Messages.get("assessment.evaluated.not");
      }
      return (Math.round(resultpointsByDutyID(assessment) / pointsByDutyID(assessment) * 10000)) / 100f + " %";
   }

   public static boolean allValuationsCorrected(long id)
   {
      Assessment assessment = Assessment.findAssessmentByID(id);
      int corrected = 0;
      for (Valuation valuation : assessment.getValuations())
      {
         if (valuation.isCorrected()) corrected++;
      }
      if (assessment.getValuations().size() == corrected) return true;
      return false;
   }
   
   public static boolean oneValuationCorrected(long id) {
      Assessment assessment = Assessment.findAssessmentByID(id);
      for (Valuation valuation : assessment.getValuations())
      {
         if (valuation.isCorrected()) return true;
      }
      return false;
   }

   public static List<Assessment> findEvaluatedAssessmentsByUserID(long id)
   {
      return find.where().eq("proofreader_id", id).findList();
   }

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public Set<Valuation> getValuations()
   {
      return valuations;
   }

   public void setValuations(Set<Valuation> valuations)
   {
      this.valuations = valuations;
   }

   public boolean addValuation(Valuation valuation)
   {
      boolean changed = valuations.add(valuation);
      if (changed && valuation != null)
      {
         valuation.setAssessment(this);
      }
      return changed;
   }

   public boolean removeValuation(Valuation valuation)
   {
      boolean changed = valuations.remove(valuation);
      if (changed && valuation != null)
      {
         valuation.setAssessment(null);
      }
      return changed;
   }

   public Duty getDuty()
   {
      return duty;
   }

   public void setDuty(Duty duty)
   {
      if (this.duty != duty)
      {
         Duty oldDuty = this.getDuty();
         this.duty = duty;
         this.update();
         if (duty == null)
         {
            oldDuty.setAssessment(this);
            oldDuty.update();
         }
         else
         {
            this.duty.setAssessment(this);
            this.duty.update();
         }
      }
   }

   public User getProofreader()
   {
      return proofreader;
   }

   public void setProofreader(User proofreader)
   {
      if (this.proofreader != proofreader)
      {
         User oldProofreader = this.proofreader;
         this.proofreader = proofreader;
         if (oldProofreader != null)
         {
            oldProofreader.removeAssessment(this);
         }
         if (proofreader != null)
         {
            proofreader.addAssessment(this);
         }
         this.update();
      }
   }
}
