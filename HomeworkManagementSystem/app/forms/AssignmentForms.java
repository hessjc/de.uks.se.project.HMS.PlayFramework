package forms;

import java.util.LinkedList;
import java.util.List;

import models.Assignment;
import models.Duty;

import org.joda.time.DateTime;

import play.data.Form;
import play.data.validation.ValidationError;

public class AssignmentForms
{

   public static class AssignmentForm extends AssignmentForms
   {
      public String name;
      public String description;
      public DateTime deadline;
      public int multiplicator;
      public String comment;
      public boolean additional;
      public Assignment assignment;

      public AssignmentForm()
      {
         this.multiplicator = 1;
      }

      public AssignmentForm(Assignment assignment)
      {
         this.assignment = assignment;
         this.name = assignment.getName();
         this.description = assignment.getDescription();
         this.deadline = assignment.getDeadline() == null ? assignment.getDeadline() : assignment.getDeadline().withHourOfDay(23).withMinuteOfHour(59)
               .withSecondOfMinute(59);
         this.multiplicator = assignment.getMultiplicator();
         this.comment = assignment.getComment();
         this.additional = assignment.isAdditional();
      }

      public List<ValidationError> validate()
      {
         deadline = deadline == null ? deadline : deadline.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (name.equals(""))
         {
            errorList.add(new ValidationError("name",
                  "Geben Sie einen Titel für das Aufgabenblatt ein."));
         }
         if (description.equals(""))
         {
            errorList.add(new ValidationError("description",
                  "Geben Sie eine Beschreibung für das Aufgabenblatt ein."));
         }
         if (!utils.DateHelper.isDateValid(deadline))
         {
            errorList.add(new ValidationError("deadline",
                  "Geben Sie ein korrektes Datum im Format (jjjj-mm-tt) ein."));
         }
         else
         {
            if (deadline.isBeforeNow())
            {
               errorList.add(new ValidationError("deadline",
                     "Das Datum muss in der Zukunft liegen."));
            }
         }
         if (multiplicator <= 0)
         {
            errorList.add(new ValidationError("multiplicator",
                  "Wert muss größer als 0 sein."));
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<AssignmentForm> AssignmentForm()
   {
      return Form.form(AssignmentForm.class);
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////////

   public static class ManualUploadForm extends AssignmentForms
   {

      public String user = "";
      public Duty duty;
      public Assignment assignment;
      public long idL;

      public ManualUploadForm()
      {
      }

      public String validate()
      {
         //to do
         return null;
      }
   }

   public static Form<ManualUploadForm> ManualUploadForm()
   {
      return Form.form(ManualUploadForm.class);
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////////	

   public static class SubtaskForm extends AssignmentForms
   {

      public long id;
      public String name;
      public String description;
      public String points;
      public boolean additional;
      public Assignment assignment;

      public SubtaskForm()
      {
      }

      public SubtaskForm(Assignment assignment)
      {
         this.assignment = assignment;
      }

      public SubtaskForm(long id, String name, String points, String description, boolean additional,
            Assignment assignment)
      {
         this.id = id;
         this.name = name;
         this.points = points;
         this.description = description;
         this.additional = additional;
         this.assignment = assignment;
      }

      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (name.equals(""))
         {
            errorList.add(new ValidationError("name",
                  "Geben Sie einen Namen für die Teilaufgabe ein."));
         }
         try
         {
            Float.parseFloat(points);
         }
         catch (NumberFormatException e)
         {
            errorList.add(new ValidationError("points",
                  "Geben Sie einen Zahlenwert ein."));
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<SubtaskForm> SubtaskForm()
   {
      return Form.form(SubtaskForm.class);
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////////

   public static class StuffForm extends AssignmentForms
   {
      public String stuffName;
      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (stuffName.equals(""))
         {
            errorList.add(new ValidationError("stuffName",
                  "Geben Sie einen Materialnamen an."));
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<StuffForm> StuffForm()
   {
      return Form.form(StuffForm.class);
   }

}
