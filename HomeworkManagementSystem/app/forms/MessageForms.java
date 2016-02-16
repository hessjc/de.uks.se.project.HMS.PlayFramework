package forms;

import java.util.LinkedList;
import java.util.List;

import play.data.Form;
import play.data.validation.ValidationError;

public class MessageForms
{ 
   public static class MessageForm extends MessageForms
   {
      public String text;
      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (text.length() < 3 || text.equals("<br>"))
         {
            errorList.add(new ValidationError("text",
                  "Die Nachricht muss aus mindestens 3 Zeichen bestehen"));
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<MessageForm> MessageForm()
   {
      return Form.form(MessageForm.class);
   }

   ///////////////////////////////////////////////////////////////////////////////////////////////////// 

   public static class AnswerForm extends MessageForms
   {
      public long id;
      public String text;
      
      public AnswerForm()
      {
      }
      
      public AnswerForm(long id)
      {
         this.id = id;
      }
      
      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (text.length() < 3 || text.equals("<br>"))
         {
            errorList.add(new ValidationError("text",
                  "Die Nachricht muss aus mindestens 3 Zeichen bestehen"));
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<AnswerForm> AnswerForm()
   {
      return Form.form(AnswerForm.class);
   }
}
