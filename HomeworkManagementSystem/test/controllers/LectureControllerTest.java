package controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.status;
import static scala.collection.JavaConversions.asJavaMap;

import java.util.HashMap;
import java.util.Map;

import models.Lecture;
import models.Roles;
import models.Semester;
import models.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.mvc.Http;
import play.mvc.Result;
import play.test.FakeRequest;
import akka.util.Timeout;

public class LectureControllerTest
{
   public static final String USER = "user@lecture.de";
   public static final String ADMIN = "admin@lecture.de";
   public static final String STUDENT = "student@lecture.de";
   public static final String LECTUREADMIN = "lectureAdmin@lecture.de";
   public static final String PROOFREADER = "proofReader@lecture.de";
   public static final String PASSWORD = "lecturePass";
   public static final String FIRSTLECTURE = "firstLecture";
   public static final String SECONDLECTURE = "secondLecture";
   public static final String SEMESTER = "WS 2010/2011";

   //Data
   public play.test.FakeApplication fakeApplication;
   private User user;
   private User student;
   private User admin;
   private User lectureAdmin;
   private User proofReader;
   private Semester semester;
   private Lecture firstLecture;
   private Lecture secondLecture;

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

   public void initData()
   {
      //User
      user = User.createUser(USER, "Test", "User", PASSWORD, "", Roles.findRoleByName("User"));
      user.setEmailValidated(true);
      user.update();
      student = User.createUser(STUDENT, "Test", "Student", PASSWORD, "12345678", Roles.findRoleByName("Student"));
      student.setEmailValidated(true);
      student.update();
      admin = User.createUser(ADMIN, "Test", "Admin", PASSWORD, "", Roles.findRoleByName("Administrator"));
      admin.setEmailValidated(true);
      admin.update();
      lectureAdmin = User.createUser(LECTUREADMIN, "Test", "Lectureadmin", PASSWORD, "", Roles.findRoleByName("User"));
      lectureAdmin.setEmailValidated(true);
      lectureAdmin.update();
      proofReader = User.createUser(PROOFREADER, "Test", "Proofreader", PASSWORD, "", Roles.findRoleByName("User"));
      proofReader.setEmailValidated(true);
      proofReader.update();

      //Semester
      semester = Semester.createSemester(SEMESTER);

      //Lecture
      firstLecture = Lecture.createLecture(FIRSTLECTURE, null, "first lecture", Semester.findSemester(SEMESTER),
            LECTUREADMIN);
      firstLecture.getProofreaders().add(proofReader);
      firstLecture.update();

   }

   /**
    ************************************* showLectures() ****************************************
    */

   @Test
   public void showLectures()
   {
      running(fakeApplication, new Runnable()
      {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testShowLectures(STUDENT, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testShowLectures(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testShowLectures(USER, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testShowLectures("", "");
            assertThat(status(result)).isEqualTo(FORBIDDEN);
         }
      });
   }

   private Result testShowLectures(String email, String password)
   {
      Http.Session session = login(email, password);
      FakeRequest request = fakeRequest();
      for (Map.Entry<String, String> e : session.entrySet())
      {
         request = request.withSession(e.getKey(), e.getValue());
      }
      Result result = callAction(
            controllers.routes.ref.LectureController.showLectures(), request);
      return result;
   }

   /**
    *************************** showLecture(Long lectureId) ****************************
    */

   @Test
   public void showExistingLectureAsStudent()
   {
      running(fakeApplication, new Runnable()
      {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testShowLecture(STUDENT, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testShowLecture(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testShowLecture(USER, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testShowLecture("", "");
            assertThat(status(result)).isEqualTo(FORBIDDEN);
         }
      });
   }

   private Result testShowLecture(String email, String password)
   {
      Http.Session session = login(email, password);
      FakeRequest request = fakeRequest();
      for (Map.Entry<String, String> e : session.entrySet())
      {
         request = request.withSession(e.getKey(), e.getValue());
      }
      Result result = callAction(
            controllers.routes.ref.LectureController.showLecture(firstLecture.id), request);
      return result;
   }

   @Test
   public void showNotExistingLecture()
   {
      running(fakeApplication, new Runnable()
      {
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
                  controllers.routes.ref.LectureController.showLecture(Lecture.findAll().size() + 1), request);
            assertThat(status(result)).isEqualTo(FORBIDDEN);
         }
      });
   }

   /**
    ***************************** createLecture() *******************************
    */

   @Test
   public void creatLecture()
   {
      running(fakeApplication, new Runnable()
      {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();

            //not logged in
            Result result = testCreateLecture("", "");
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            //logged in as user
            result = testCreateLecture(USER, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            //logged in as student
            result = testCreateLecture(STUDENT, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            //logged in as admin
            result = testCreateLecture(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);
         }
      });
   }

   private Result testCreateLecture(String email, String password)
   {
      Http.Session session = login(email, password);
      FakeRequest request = fakeRequest();
      for (Map.Entry<String, String> e : session.entrySet())
      {
         request = request.withSession(e.getKey(), e.getValue());
      }
      Result result = callAction(
            controllers.routes.ref.LectureController.createLecture(), request);
      return result;
   }

   /**
    *************************** editLecture ************************
    */

   @Test
   public void editNotExisitngLecture()
   {
      running(fakeApplication, new Runnable()
      {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Http.Session session = login(ADMIN, PASSWORD);
            FakeRequest request = fakeRequest();
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            Result result = callAction(
                  controllers.routes.ref.LectureController.editLecture(Lecture.findAll().size() + 1), request);
            assertThat(status(result)).isEqualTo(FORBIDDEN);
         }
      });
   }

   @Test
   public void editExisitngLecture()
   {
      running(fakeApplication, new Runnable()
      {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testEditLecture("", "");
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testEditLecture(STUDENT, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testEditLecture(USER, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testEditLecture(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testEditLecture(LECTUREADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);
         }
      });
   }

   private Result testEditLecture(String email, String password)
   {
      FakeRequest request = fakeRequest("get", routes.LectureController.editLecture(firstLecture.id).url());
      if (!email.equals(""))
      {
         Http.Session session = login(email, password);
         for (Map.Entry<String, String> e : session.entrySet())
         {
            request = request.withSession(e.getKey(), e.getValue());
         }
      }
      Result result = callAction(
            controllers.routes.ref.LectureController.editLecture(firstLecture.id), request);
      return result;
   }

   /**
    * ********************** joinLecture(Long lectureId) **********************
    */

   @Test
   public void joinNotExisitngLecture()
   {
      running(fakeApplication, new Runnable()
      {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Http.Session session = login(STUDENT, PASSWORD);
            FakeRequest request = fakeRequest();
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            Result result = callAction(
                  controllers.routes.ref.LectureController.joinLecture(Lecture.findAll().size() + 1), request);
            assertThat(status(result)).isEqualTo(FORBIDDEN);
         }
      });
   }

   @Test
   public void joinExisitngLecture()
   {
      running(fakeApplication, new Runnable()
      {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Http.Session session = login(STUDENT, PASSWORD);
            FakeRequest request = fakeRequest("get", routes.LectureController.joinLecture(firstLecture.id).url());
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            Result result = callAction(
                  controllers.routes.ref.LectureController.joinLecture(firstLecture.id), request);
            assertThat(status(result)).isEqualTo(303);
         }
      });
   }

}
