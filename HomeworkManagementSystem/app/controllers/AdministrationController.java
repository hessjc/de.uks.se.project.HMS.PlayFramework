package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import security.HDSDynamicResourceHandler;
import be.objectify.deadbolt.java.actions.Dynamic;

public class AdministrationController extends Controller
{

   /**
    * render the roles-view
    * 
    * @return roles-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result administrateRoles()
   {
      return ok(views.html.admin.roles.render());
   }

   /**
    * render the users-view
    * 
    * @return users-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result administrateUsers()
   {
      return ok(views.html.admin.users.render());
   }

   /**
    * render the lectures-view
    * 
    * @return lectures-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result administrateLectures()
   {
      return ok(views.html.admin.lectures.render());
   }

   /**
    * render the dutys-view
    * 
    * @return dutys-view
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result administrateDutys()
   {
      return ok(views.html.admin.dutys.render());
   }

}
