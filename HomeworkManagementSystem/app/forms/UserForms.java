package forms;

import play.data.Form;
import provider.SimpleUserPassAuthProvider.SimpleAuthSignup;
import provider.SimpleUserPassAuthProvider.SimpleLogin;
import forms.AccountForms.PasswordForm;

public class UserForms
{

   public static Form<SimpleLogin> LoginForm()
   {
      return Form.form(SimpleLogin.class);
   }

   public static Form<SimpleAuthSignup> RegistrationForm()
   {
      return Form.form(SimpleAuthSignup.class);
   }

   public static Form<PasswordResetForm> PasswordResetForm()
   {
      return Form.form(PasswordResetForm.class);
   }

   public static class PasswordResetForm extends PasswordForm
   {
      public String token;

      public PasswordResetForm()
      {
      }

      public String getToken()
      {
         return token;
      }

      public void setToken(String token)
      {
         this.token = token;
      }
   }
}
