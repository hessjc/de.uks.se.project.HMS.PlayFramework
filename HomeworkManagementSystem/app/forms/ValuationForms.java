package forms;

import java.util.LinkedList;
import java.util.List;

import play.data.Form;
import play.data.validation.ValidationError;

public class ValuationForms
{

   public static class ValuationForm extends ValuationForms
   {

      public float resultpoints;
      public float points;
      public String comment;

      public ValuationForm()
      {
         // TODO Auto-generated constructor stub
      }

      public ValuationForm(float resultpoints, String comment)
      {
         this.resultpoints = resultpoints;
         this.comment = comment;
      }

      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (resultpoints < 0 || resultpoints > points)
         {
            errorList.add(new ValidationError("points",
                  "Erreichte Punkte dürfen nur Werte zwischen 0 und Maximalpunktzahl erreichen"));
         }

         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<ValuationForm> ValuationForm()
   {
      return Form.form(ValuationForm.class);
   }
}