package controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.status;
import static scala.collection.JavaConversions.asJavaMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import models.Assignment;
import models.Lecture;
import models.Roles;
import models.Semester;
import models.Stuff;
import models.Subtask;
import models.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.mvc.Http;
import play.mvc.Result;
import play.test.FakeRequest;
import utils.FileHelper;
import akka.util.Timeout;

import com.avaje.ebean.Ebean;

public class SubtaskControllerTest
{

   public static final String USER = "user@lecture.de";
   public static final String ADMIN = "admin@lecture.de";
   public static final String STUDENT = "student@lecture.de";
   public static final String STUDENTOFFIRSTLECTURE = "studentOfFirstLecture@lecture.de";
   public static final String LECTUREADMIN = "lectureAdmin@lecture.de";
   public static final String PROOFREADER = "proofReader@lecture.de";
   public static final String PASSWORD = "lecturePass";
   public static final String FIRSTLECTURE = "firstLecture";
   public static final String SECONDLECTURE = "secondLecture";
   public static final String FIRSTASSIGNMENT = "firstAssignment";
   public static final String SECONDASSIGNMENT = "secondAssignment";
   public static final String SEMESTER = "WS 2010/2011";

   /**
    * Data
    */
   //User
   public play.test.FakeApplication fakeApplication;
   private User user;
   private User student;
   private User studentOfFirstLecture;
   private User admin;
   private User lectureAdmin;
   private User proofReader;

   //Semester
   private Semester semester;

   //Lecture
   private Lecture firstLecture;
   private Lecture secondLecture;

   //Assignment
   private Assignment firstAssignment;
   private Assignment secondAssignment;

   //Subtask
   private Subtask firstSubtask;
   private Subtask secondSubtask;

   //Stuff
   private Stuff stuff;

   //File
   private File file;

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
      studentOfFirstLecture = User.createUser(STUDENTOFFIRSTLECTURE, "Test", "StudentOfFirstLecture", PASSWORD,
            "87654321", Roles.findRoleByName("Student"));
      studentOfFirstLecture.setEmailValidated(true);
      studentOfFirstLecture.update();
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
      Ebean.saveManyToManyAssociations(firstLecture, "proofreaders");
      firstLecture.getUsers().add(studentOfFirstLecture);
      Ebean.saveManyToManyAssociations(firstLecture, "users");

      //Assignment
      firstAssignment = Assignment.createAssignment(FIRSTASSIGNMENT, "first Assignment", null, 1, "test", false,
            firstLecture);
      firstAssignment.setCompleted(true);
      firstAssignment.update();
      secondAssignment = Assignment.createAssignment(SECONDASSIGNMENT, "second Assignment", null, 1, "test", false,
            firstLecture);

      //Subtask
      firstSubtask = Subtask.createSubtask("first Subtask", "first Subtask", 5.0f, false, firstAssignment);
      secondSubtask = Subtask.createSubtask("second Subtask", "second subtask", 4.0f, false, secondAssignment);

      //Stuff
      file = new File("test.txt");
      try
      {
         file.createNewFile();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      File destFile = AssignmentController.getStuffFile(firstAssignment, "test", "txt");
      FileHelper.copyFile(file, destFile);
      stuff = Stuff.createStuff("test", "txt", firstAssignment);

   }

   /**
    ************************ createSubtask(lectureId, assignmentId) ***********************
    */
   @Test
   public void createSubtask()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testCreateSubtask(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testCreateSubtask(LECTUREADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testCreateSubtask(PROOFREADER, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testCreateSubtask(USER, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testCreateSubtask(STUDENT, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testCreateSubtask(STUDENTOFFIRSTLECTURE, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);
         }

         private Result testCreateSubtask(String email, String password)
         {
            Http.Session session = login(email, password);
            FakeRequest request = fakeRequest("get", routes.SubtaskController
                  .createSubtask(firstLecture.id, secondAssignment.id).url());
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            assertThat(secondAssignment.isCompleted()).isEqualTo(false);
            Result result = callAction(
                  controllers.routes.ref.SubtaskController
                        .createSubtask(firstLecture.id, secondAssignment.id), request);
            return result;
         }
      });
   }

   @Test
   public void createSubtaskOfCompletedAssignment()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testCreateSubtaskOfCompletedAssignment(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(SEE_OTHER);

            result = testCreateSubtaskOfCompletedAssignment(LECTUREADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(SEE_OTHER);

         }

         private Result testCreateSubtaskOfCompletedAssignment(String email, String password)
         {
            Http.Session session = login(email, password);
            FakeRequest request = fakeRequest("get", routes.SubtaskController
                  .createSubtask(firstLecture.id, firstAssignment.id).url());
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            assertThat(firstAssignment.isCompleted()).isEqualTo(true);
            Result result = callAction(
                  controllers.routes.ref.SubtaskController
                        .createSubtask(firstLecture.id, firstAssignment.id), request);
            return result;
         }
      });
   }

   @Test
   public void createSubtaskWithWrongLecture()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testCreateSubtaskkWithWrongLecture(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testCreateSubtaskkWithWrongLecture(LECTUREADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

         }

         private Result testCreateSubtaskkWithWrongLecture(String email, String password)
         {
            Http.Session session = login(email, password);
            FakeRequest request = fakeRequest("get", routes.SubtaskController
                  .createSubtask(firstLecture.id + 1, firstAssignment.id).url());
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            assertThat(secondAssignment.isCompleted()).isEqualTo(false);
            Result result = callAction(
                  controllers.routes.ref.SubtaskController
                        .createSubtask(firstLecture.id + 1, firstAssignment.id), request);
            return result;
         }
      });
   }

   /**
    ***************** editSubtask(lectureId, assignmentId, subtaskId) ******************
    */
   @Test
   public void editSubtask()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testEditSubtask(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testEditSubtask(LECTUREADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(OK);

            result = testEditSubtask(PROOFREADER, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testEditSubtask(USER, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testEditSubtask(STUDENT, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testEditSubtask(STUDENTOFFIRSTLECTURE, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);
         }

         private Result testEditSubtask(String email, String password)
         {
            Http.Session session = login(email, password);
            FakeRequest request = fakeRequest("get", routes.SubtaskController
                  .editSubtask(firstLecture.id, secondAssignment.id, secondSubtask.id).url());
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            assertThat(secondAssignment.isCompleted()).isEqualTo(false);
            Result result = callAction(
                  controllers.routes.ref.SubtaskController
                        .editSubtask(firstLecture.id, secondAssignment.id, secondSubtask.id), request);
            return result;
         }
      });
   }

   @Test
   public void editSubtaskWithCompletedAssignment()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testEditSubtaskWithCompletedAssignment(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(SEE_OTHER);

            result = testEditSubtaskWithCompletedAssignment(LECTUREADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(SEE_OTHER);
         }

         private Result testEditSubtaskWithCompletedAssignment(String email, String password)
         {
            Http.Session session = login(email, password);
            FakeRequest request = fakeRequest("get", routes.SubtaskController
                  .editSubtask(firstLecture.id, firstAssignment.id, firstSubtask.id).url());
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            assertThat(firstAssignment.isCompleted()).isEqualTo(true);
            Result result = callAction(
                  controllers.routes.ref.SubtaskController
                        .editSubtask(firstLecture.id, firstAssignment.id, firstSubtask.id), request);
            return result;
         }
      });
   }

   @Test
   public void editSubtaskWithWrongLecture()
   {
      running(fakeApplication, new Runnable() {
         @Override
         public void run()
         {
            assertThat(SimpleUserServicePlugin()).isNotNull();
            Result result = testEditSubtaskkWithWrongLecture(ADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

            result = testEditSubtaskkWithWrongLecture(LECTUREADMIN, PASSWORD);
            assertThat(status(result)).isEqualTo(FORBIDDEN);

         }

         private Result testEditSubtaskkWithWrongLecture(String email, String password)
         {
            Http.Session session = login(email, password);
            FakeRequest request = fakeRequest("get", routes.SubtaskController
                  .editSubtask(firstLecture.id + 1, firstAssignment.id, firstSubtask.id).url());
            for (Map.Entry<String, String> e : session.entrySet())
            {
               request = request.withSession(e.getKey(), e.getValue());
            }
            assertThat(secondAssignment.isCompleted()).isEqualTo(false);
            Result result = callAction(
                  controllers.routes.ref.SubtaskController
                        .editSubtask(firstLecture.id + 1, firstAssignment.id, firstSubtask.id), request);
            return result;
         }
      });
   }
}
