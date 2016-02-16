package controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.status;
import static scala.collection.JavaConversions.asJavaMap;

import java.util.HashMap;
import java.util.Map;

import models.Roles;
import models.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.mvc.Http;
import play.mvc.Result;
import play.test.FakeRequest;
import util.InMemoryModel;
import akka.util.Timeout;

public class UserControllerTest
{
   public static final String USER = "user@lecture.de";
   public static final String PASSWORD = "lecturePass";
   
   //Data
   public play.test.FakeApplication fakeApplication;
   private User user;
   
   /**
    * Executes a block of code in a running application.
    */
   public synchronized void running(play.test.FakeApplication fakeApplication, final Runnable block)
   {
      block.run();
   }

   @Before
   public void setUp()
   {
      fakeApplication = fakeApplication(inMemoryDatabase());
      start(fakeApplication);
      initData();
   }

   /**
    * Starts an application.
    */
   public void start(play.test.FakeApplication fakeApplication)
   {
      play.api.Play.start(fakeApplication.getWrappedApplication());
   }

   /**
    * Stops an application.
    */
   public void stop(play.test.FakeApplication fakeApplication)
   {
      play.api.Play.stop();
   }

   private service.SimpleUserServicePlugin SimpleUserServicePlugin()
   {
      return Play.application().plugin(service.SimpleUserServicePlugin.class);
   }
   
   public void initData()
   {
      //User
      user = User.createUser(USER, "Test", "User", PASSWORD, "", Roles.findRoleByName("User"));
      user.setEmailValidated(true);
      user.update();
   }
   
   @Test
   public void redirectsWhenNotLoggedIn()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = callAction(controllers.routes.ref.Application
                  .index());
            assertThat(status(result)).isEqualTo(OK);
         }
      });
   }

   @Test
   public void okWhenLoggedIn()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Http.Session session = login(USER, PASSWORD);
            FakeRequest request = fakeRequest();
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            Result result = callAction(
                  controllers.routes.ref.AccountController.showMyAccountInfo(), request);
            assertThat(status(result)).isEqualTo(OK);
         }
      });
   }
   
   @Test
   public void forrbiddenWhenNotLoggedIn()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            FakeRequest request = fakeRequest();
            Result result = callAction(
                  controllers.routes.ref.AccountController.showMyAccountInfo(), request);
            assertThat(status(result)).isEqualTo(403);
         }
      });
   }

   private Http.Session login(String email, String password)
   {
      {
         // Log the user in
         Map<String, String> data = new HashMap<String, String>();
         data.put("login", email);
         data.put("password", password);
         Result result = callAction(
               controllers.routes.ref.UserController.login(), fakeRequest()
                     .withFormUrlEncodedBody(data));
         Assert.assertNotNull(result);
         Map<String, String> sessionData = asJavaMap(play.api.test.Helpers
               .session(result.getWrappedResult(), Timeout.apply(30000))
               .data());
         Assert.assertNotNull(sessionData);
         return new Http.Session(sessionData);
      }
   }

}
