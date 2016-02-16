package forms;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import models.Semester;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;

public class SemesterForms
{

   public static Form<CreateSemesterForm> CreateSemesterForm()
   {
      return Form.form(CreateSemesterForm.class);
   }

   public static class CreateSemesterForm extends SemesterForms
   {

      public String semester;

      public CreateSemesterForm()
      {
      }

      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();

         boolean isSS = Semester.SS_Pattern.matcher(semester).find();
         Matcher wsMatcher = Semester.WS_Pattern.matcher(semester);
         boolean isWS = wsMatcher.find();
         if (!isSS && !isWS)
         {
            errorList.add(new ValidationError("semester", Messages.get("semester.create.error.wrongformat")));
         }

         if (isWS)
         {
            String startYear = wsMatcher.group(1);
            String endYear = wsMatcher.group(2);
            if (Integer.parseInt(startYear) + 1 != Integer.parseInt(endYear))
            {
               errorList.add(new ValidationError("semester", Messages.get("semester.create.error.wsinvalidyears")));
            }
         }

         return errorList.isEmpty() ? null : errorList;
      }
   }
}
