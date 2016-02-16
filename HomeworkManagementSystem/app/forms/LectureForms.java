package forms;

import java.util.LinkedList;
import java.util.List;

import models.Lecture;

import org.joda.time.DateTime;

import play.data.Form;
import play.data.validation.Constraints.Required;
import play.data.validation.ValidationError;

public class LectureForms
{

   public static class CreateLectureForm extends LectureForms
   {
      public String name;
      public String semester;
      public DateTime closingdate;
      public String lectureadmin;
      public String description;

      public int optionalDutys;
      public int lowerProcentualBounderyOfDutys;
      
      public int minimumPercentageForExamination;
      public String bonuses;

      public CreateLectureForm(String lectureadmin)
      {
         this.lectureadmin = lectureadmin;
      }

      public CreateLectureForm()
      {
      }

      public List<ValidationError> validate()
      {
         closingdate = closingdate == null ? closingdate : closingdate.withHourOfDay(23).withMinuteOfHour(59)
               .withSecondOfMinute(59);
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (name.equals(""))
         {
            errorList.add(new ValidationError("name",
                  "Geben Sie einen Titel für die Veranstaltung ein."));
         }
         if (description.equals("") || description.equals("<br>"))
         {
            errorList.add(new ValidationError("description",
                  "Geben Sie eine Beschreibung für die Veranstaltung ein."));
         }
         if (closingdate != null)
         {
            if (!utils.DateHelper.isDateValid(closingdate))
            {
               errorList.add(new ValidationError("closingdate",
                     "Geben Sie ein korrektes Datum im Format (jjjj-mm-tt) ein."));
            }
            else
            {
               if (closingdate.isBeforeNow())
               {
                  errorList.add(new ValidationError("closingdate",
                        "Das Datum muss in der Zukunft liegen."));
               }
            }
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<CreateLectureForm> CreateLectureForm()
   {
      return Form.form(CreateLectureForm.class);
   }

   ///////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////

   public static class EditLectureForm extends LectureForms
   {
      public Long id;
      public String name;
      public String semester;
      public DateTime closingdate;
      public String description;

      public int optionalDutys;
      public int lowerProcentualBounderyOfDutys;
      
      public int minimumPercentageForExamination;
      public String bonuses;

      public EditLectureForm(Lecture lecture)
      {
         this.id = lecture.getId();
         this.name = lecture.getName();
         this.semester = lecture.getSemester().getSemester();
         this.closingdate = lecture.getClosingdate() == null ? lecture.getClosingdate() : lecture.getClosingdate()
               .withHourOfDay(23).withMinuteOfHour(59)
               .withSecondOfMinute(59);
         this.description = lecture.getDescription();
         this.optionalDutys = lecture.getOptionalDutys();
         this.lowerProcentualBounderyOfDutys = lecture.getLowerProcentualBounderyOfDutys();
         this.minimumPercentageForExamination = lecture.getMinimumPercentageForExamination();
      }

      public EditLectureForm()
      {
      }

      public List<ValidationError> validate()
      {
         closingdate = closingdate == null ? closingdate : closingdate.withHourOfDay(23).withMinuteOfHour(59)
               .withSecondOfMinute(59);
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (name.equals(""))
         {
            errorList.add(new ValidationError("name",
                  "Geben Sie einen Titel für die Veranstaltung ein."));
         }
         if (description.equals(""))
         {
            errorList.add(new ValidationError("description",
                  "Geben Sie eine Beschreibung für die Veranstaltung ein."));
         }
         if (closingdate != null)
         {
            if (!utils.DateHelper.isDateValid(closingdate))
            {
               errorList.add(new ValidationError("closingdate",
                     "Geben Sie ein korrektes Datum im Format (jjjj-mm-tt) ein."));
            }
            else
            {
               if (closingdate.isBeforeNow())
               {
                  errorList.add(new ValidationError("closingdate",
                        "Das Datum muss in der Zukunft liegen."));
               }
            }
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<EditLectureForm> EditLectureForm()
   {
      return Form.form(EditLectureForm.class);
   }

   ///////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////

   public static class AddForm extends LectureForms
   {

      public String name;

      public String validate()
      {
         return null;
      }
   }

   public static Form<AddForm> AddForm()
   {
      return Form.form(AddForm.class);
   }

   ///////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////

   public static class SendEmailToAllForm
   {
      @Required
      public String subject;
      @Required
      public String text;

      public String validate()
      {
         return null;
      }

      public String getSubject()
      {
         return subject;
      }

      public void setSubject(String subject)
      {
         this.subject = subject;
      }

      public String getText()
      {
         return text;
      }

      public void setText(String text)
      {
         this.text = text;
      }
   }

   public static Form<SendEmailToAllForm> SendEmailToAllForm()
   {
      return Form.form(SendEmailToAllForm.class);
   }

}
