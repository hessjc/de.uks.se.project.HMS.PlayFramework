package forms;

import java.util.LinkedList;
import java.util.List;

import models.User;
import play.data.Form;
import play.data.validation.ValidationError;

public class AccountForms
{

   public static class StudentForm extends AccountForms
   {

      public Long id;
      public String matrikelNumber;

      public StudentForm(Long id, String matrikelNumber)
      {
         this.id = id;
         this.matrikelNumber = matrikelNumber;
      }

      public StudentForm()
      {
      }

      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (matrikelNumber.equals(""))
         {
            errorList.add(new ValidationError("matrikelnumber",
                  "Die Matrikelnummer darf kein leerer String sein"));
         }
         if (User.findUserByMatrikelNumber(matrikelNumber) != null
               && !User.findUserByID(id).equals(User.findUserByMatrikelNumber(matrikelNumber)))
         {
            errorList.add(new ValidationError("matrikelnumber",
                  "Die Matrikelnummer ist nicht gültig."));
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static class PasswordForm extends AccountForms
   {

      public Long id;
      public String password;
      public String passwordRepeat;

      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (password == null || "".equals(password.trim()))
         {
            errorList.add(new ValidationError("password",
                  "Das Passwort darf nicht leer sein."));
         }
         if (passwordRepeat == null || "".equals(passwordRepeat.trim()))
         {
            errorList.add(new ValidationError("passwordRepeat",
                  "Das Passwort (Wiederholung) darf nicht leer sein."));
         }
         if (errorList.isEmpty() && !password.equals(passwordRepeat))
         {
            errorList.add(new ValidationError("passwordMismatch", "Die Passwörter stimmen nicht überein"));
         }

         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<StudentForm> StudentForm()
   {
      return Form.form(StudentForm.class);
   }

   public static Form<PasswordForm> PasswordForm()
   {
      return Form.form(PasswordForm.class);
   }

   ///////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////

   public static class EmailForm extends AccountForms
   {

      public Long id;
      public String emailNew;
      public String emailVal;

      public EmailForm(Long id, String emailNew)
      {
         this.id = id;
         this.emailNew = emailNew;
      }

      public EmailForm()
      {
      }

      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (emailNew.equals(""))
         {
            errorList.add(new ValidationError("emailnew",
                  "Die Email darf kein leerer String sein"));
         }
         if (emailVal.equals(""))
         {
            errorList.add(new ValidationError("emailval",
                  "Die Email Adressen stimmen nicht überein."));
         }
         else if (!emailNew.equals(emailVal))
         {
            errorList.add(new ValidationError("emailval",
                  "Die Email Adressen stimmen nicht überein."));
         }
         else if (User.findUserByEmail(emailNew) != null
               && !User.findUserByID(id).equals(User.findUserByEmail(emailNew)))
         {
            errorList.add(new ValidationError("emailval",
                  "Die Email Adresse ist nicht gültig."));
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<EmailForm> EmailForm()
   {
      return Form.form(EmailForm.class);
   }

   ///////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////

   public static class NameForm extends AccountForms
   {

      public Long id;
      public String firstName;
      public String lastName;

      public NameForm(Long id, String firstName, String lastName)
      {
         this.id = id;
         this.firstName = firstName;
         this.lastName = lastName;
      }

      public NameForm()
      {
      }

      public List<ValidationError> validate()
      {
         List<ValidationError> errorList = new LinkedList<ValidationError>();
         if (firstName.equals(""))
         {
            errorList.add(new ValidationError("firstname",
                  "Der Vorname ist nicht gültig"));
         }
         if (lastName.equals(""))
         {
            errorList.add(new ValidationError("lastname",
                  "Der Nachname ist nicht gültig"));
         }
         return errorList.isEmpty() ? null : errorList;
      }
   }

   public static Form<NameForm> NameForm()
   {
      return Form.form(NameForm.class);
   }
}
