package forms;

import play.data.Form;

public class SimpleForms
{
   public static Form<IDForm> IDForm()
   {
      return Form.form(IDForm.class);
   }

   public static class IDForm
   {
      public long id;

      public IDForm()
      {
      }
   }
   
   public static Form<EmailForm> EmailForm()
   {
      return Form.form(EmailForm.class);
   }
   
   public static class EmailForm
   {
      public String email;

      public EmailForm()
      {
      }
   }
}
