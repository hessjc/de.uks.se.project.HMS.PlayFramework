package utils;

import java.util.List;
import java.util.Map.Entry;

import play.data.Form;
import play.data.validation.ValidationError;

public class ErrorHelper
{
   public static void handleErrors(final Form<?> form,
         final ErrorHandler errorWriter)
   {
      if (form.hasGlobalErrors())
      {
         for (ValidationError validationError : form.globalErrors())
         {
            errorWriter.handleGlobalError(validationError);
         }
      }

      if (form.hasErrors())
      {
         for (Entry<String, List<ValidationError>> entry : form.errors()
               .entrySet())
         {
            String key = entry.getKey();
            if (key != null && !key.isEmpty())
            {
               errorWriter.handleError(key, form.error(key));
            }
         }
      }
   }

   public abstract static class ErrorHandler
   {
      public void handleGlobalError(final ValidationError error)
      {

      }

      public abstract void handleError(final String identifier,
            final ValidationError error);
   }
}
