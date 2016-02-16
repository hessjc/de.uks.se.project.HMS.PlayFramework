package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import security.HDSDynamicResourceHandler;
import be.objectify.deadbolt.java.actions.Dynamic;
import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.feth.play.module.pa.PlayAuthenticate;

import forms.UserForms;

/**
 * Homework Management System - First entry point
 * Application class for authentication and authorization
 * 
 * @version 1.0
 */
public class Application extends Controller
{

   /**
    * @return index-view (user is not logged in)
    * @return home-view (user is logged in)
    */
   public static Result index()
   {
      if (!SessionAvailable())
      {
         return ok(views.html.index.render());
      }
      else
      {
         return ok(views.html.lecture.lectures.render());
      }
   }

   /**
    * @return boolean value if session is available
    */
   @SubjectPresent
   public static boolean SessionAvailable()
   {
      return PlayAuthenticate.isLoggedIn(session());
   }

   /**
    * changelog
    */
   @Dynamic(HDSDynamicResourceHandler.ADMINISTRATOR)
   public static Result changelog()
   {
      return ok(views.html.site.changelog.render());
   }

   /**
    * frequently asked questions
    */
   public static Result faq()
   {
      return ok(views.html.site.faq.render(UserForms.LoginForm()));
   }

   /**
    * all available links
    */
   public static Result sitemap()
   {
      return ok(views.html.site.sitemap.render(UserForms.LoginForm()));
   }

   /**
    * contact information
    */
   public static Result impressum()
   {
      return ok(views.html.site.impressum.render(UserForms.LoginForm()));
   }

   /**
    * policies and usage
    */
   public static Result privacy()
   {
      return ok(views.html.site.privacy.render(UserForms.LoginForm()));
   }

   public static Result plus(String index)
   {
      return ok(views.html.lecture.additional.examination.render(Integer.valueOf(index)));
   }

   public static Result minus(String index)
   {
      return ok(views.html.lecture.additional.examination.render(Integer.valueOf(index)));
   }
   
   public static Result refreshGlobalMessage() {
      return ok(views.html.message.alert.render());
   }

   public static Result javascriptRoutes()
   {
      response().setContentType("text/javascript");
      return ok(Routes.javascriptRouter("jsRoutes",
            // Routes
            controllers.routes.javascript.Application.plus(),
            controllers.routes.javascript.Application.minus(),
            
            controllers.routes.javascript.Application.refreshGlobalMessage(),

            controllers.routes.javascript.DutyController.allocate(),
            controllers.routes.javascript.DutyController.release(),
            controllers.routes.javascript.DutyController.refreshCatchedDutys(),
            
            controllers.routes.javascript.DutyController.allocate1(),
            controllers.routes.javascript.DutyController.release1(),
            controllers.routes.javascript.DutyController.refreshCatchedDutys1()
            ));
   }
}