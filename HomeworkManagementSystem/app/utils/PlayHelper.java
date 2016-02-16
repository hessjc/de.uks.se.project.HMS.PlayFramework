package utils;

import play.Play;
import play.mvc.Http.Request;
import play.mvc.Http.Response;


public class PlayHelper
{
   private static final String X_PJAX = "X-PJAX";
   private static final String X_PJAX_URL = "X-PJAX-URL";
   private static final String X_PJAX_EXTRA = "X-PJAX-EXTRA";
   private static final String PJAX_EXTRA_FORM_VALIDATION_ERROR = "FORM_VALIDATION_ERROR";

   /**
    * Checks if the given request is a pjax request
    * @param request
    * @return <code>true</code> if the given request is a pjax request, <code>false</code>
    * otherwise
    */
   public static boolean isPjaxRequest(Request request)
   {
      return request.getHeader(X_PJAX) != null;
   }
   
   public static Response setPjaxResponseURL(Response response, String url)
   {
      response.setHeader(X_PJAX_URL, url);
      return response;
   }
   
   /**
    * Sets pjax specific header information to signal that the response contains
    * an error.
    * @param response
    * @return
    */
   public static Response pjaxFormError(Response response)
   {
      response.setHeader(X_PJAX_EXTRA, PJAX_EXTRA_FORM_VALIDATION_ERROR);
      return response;
   }
   
   public static String context()
   {
      String contextPath = Play.application().configuration().getString("application.context");
      if (contextPath == null)
      {
         contextPath = "/";
      }
      return contextPath;
   }
}
