package utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import models.User;
import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.mvc.Http.Context;

import com.feth.play.module.mail.Mailer;
import com.feth.play.module.mail.Mailer.Mail;
import com.feth.play.module.mail.Mailer.Mail.Body;

public class MailUtil implements Constants
{
   public static final String EMPTY_EMAIL = "views.%s.email.empty_%s";

   private static Mailer mailer;

   public static boolean sendMail(final Context context, final User recipient, final String subject,
         final String template, final Object... renderParams)
   {
      return sendMails(context, Collections.singleton(recipient), subject, template, renderParams);
   }

   public static boolean sendMails(final Context context, final Collection<User> users, final String subject,
         final String template, final Object... renderParams)
   {
      try
      {
         String language = Lang.preferred(context.request().acceptLanguages()).code();

         final String html = getEmailTemplate(String.format(template, "html", language), renderParams);
         final String text = getEmailTemplate(String.format(template, "txt", language), renderParams);

         final Body body = new Body(text, html);

         for (User user : users)
         {
            sendMail(new Mail(subject, body, new String[] { user.getEmail() }));
         }
         return true;
      }
      catch (Exception e)
      {
         Logger.error(String.format("Mail for template '%s' could not be sent!", template), e);
      }
      return false;
   }

   private static String getEmailTemplate(final String template, final Object... renderParams)
         throws IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException,
         IllegalAccessException
   {
      Class<?> templateClass = null;
      try
      {
         templateClass = Class.forName(template);
      }
      catch (ClassNotFoundException e)
      {
         Logger.warn(String.format("Template: '%s' was not found!", template), e);
      }

      Class<?>[] renderClasses = new Class[renderParams.length];
      int i = 0;
      for (Object object : renderParams)
      {
         renderClasses[i++] = object.getClass();
      }

      String ret = null;
      if (templateClass != null)
      {
         Method htmlRender = templateClass.getMethod("render", renderClasses);
         ret = htmlRender.invoke(null, renderParams).toString();
      }
      return ret;
   }

   public static void sendMail(final Mail mail)
   {
      try
      {
         getMailer().sendMail(mail);
         Logger.debug(String.format("Mail with subject %s was sent to %s", mail.getSubject(),
               Arrays.toString(mail.getRecipients())));
      }
      catch (Exception e)
      {
         Logger.error(String.format("Mail to %s was not sent", Arrays.toString(mail.getRecipients())), e);
      }
   }

   public static Mailer getMailer()
   {
      if (mailer == null)
      {
         Mailer customMailer = Mailer.getCustomMailer(Play.application().configuration()
               .getConfig(MAIL_CONFIGURATION_KEY));
         mailer = customMailer;
      }
      return mailer;
   }
}
