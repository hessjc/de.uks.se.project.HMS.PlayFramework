package util;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import models.Assessment;
import models.Assignment;
import models.Duty;
import models.Lecture;
import models.Roles;
import models.Semester;
import models.Stuff;
import models.Subtask;
import models.User;
import models.Valuation;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

public class InMemoryModel
{
   public static String roleNAME = "TestRolle";

   public static String userEMAIL = "user@email.de";
   public static String userFIRSTNAME = "firstname";
   public static String userLASTNAME = "lastname";
   public static String userPASSWORD = "password";
   public static String userStudentEMAIL = "student@email.de";
   public static String userStudentMATRIKELNUMBER = "uk123456";

   public static String semesterNAME = "WS 2098/2099";
   public static String semesterOtherName = "SS 2015";

   public static String lectureNAME = "Name der Veranstaltung";
   public static String lectureDESCRIPTION = "Beschreibung der Veranstaltung";
   public static DateTime lectureCLOSINGDATE = new DateTime();

   public static String assignmentNAME = "Name des Aufgabenblattes";
   public static String assignmentDESCRIPTION = "Beschreibung des Aufgabenblattes";
   public static DateTime assignmentDEADLINE = new DateTime();
   public static int assignmentMULTIPLICATOR = 1;
   public static String assignmentCOMMENT = "Kommentar zum Aufgabenblatt";
   public static boolean assignmentADDITIONAL = false;

   public static String stuffFILENAME = "Filename_12345";
   public static String stuffTYPE = ".zip";

   public static String subtaskNAME = "Name der Teilaufgabe";
   public static String subtaskDESCRIPTION = "Beschreibung der Teilaufgabe";
   public static float subtaskPOINTS = 3.5f;
   public static boolean subtaskADDITIONAL = false;

   public static String dutyUPLOADEDFILE = "/path/to/directory/of/users/upload/";
   public static DateTime dutyUPLOADEDDATETIME = new DateTime();

   public static Roles role;
   public static User user;
   public static User student;
   public static User lectureadmin;
   public static User proofreader;
   public static Semester semester;
   public static Semester othersemester;
   public static Lecture lecture;
   public static Assignment assignment;
   public static Stuff stuff;
   public static Subtask subtask;
   public static Duty duty;
   public static Assessment assessment;
   public static Valuation valuation;
   public static play.test.FakeApplication fakeApplication;

   /**
    * Starts an application.
    */
   public static synchronized void start(play.test.FakeApplication fakeApplication)
   {
      play.api.Play.start(fakeApplication.getWrappedApplication());
   }

   /**
    * Stops an application.
    */
   public static synchronized void stop(play.test.FakeApplication fakeApplication)
   {
      play.api.Play.stop();
   }

   /**
    * Executes a block of code in a running application.
    */
   public static synchronized void running(play.test.FakeApplication fakeApplication, final Runnable block)
   {
      block.run();
   }

   @Before
   public void setUp()
   {
      fakeApplication = fakeApplication(inMemoryDatabase());
      start(fakeApplication);
      initDatas();
   }

   @After
   public void stopApp()
   {
      stop(fakeApplication);
   }

   public static void initDatas()
   {
      // init roles
      role = Roles.createRole(roleNAME);

      //init users
      user = User.createUser(userEMAIL, userFIRSTNAME, userLASTNAME, userPASSWORD, "", role);
      student = User.createUser(userStudentEMAIL, userFIRSTNAME, userLASTNAME, userPASSWORD, userStudentMATRIKELNUMBER,
            role);

      lectureadmin = User.createUser("lectureadmin@email.de", userFIRSTNAME, userLASTNAME, userPASSWORD, "", role);
      proofreader = User.createUser("proofreader@email.de", userFIRSTNAME, userLASTNAME, userPASSWORD, "", role);

      //init semesters
      semester = Semester.createSemester(semesterNAME);
      othersemester = Semester.createSemester(semesterOtherName);

      //init lectures
      lecture = Lecture.createLecture(lectureNAME, lectureCLOSINGDATE, lectureDESCRIPTION, semester,
            lectureadmin.getEmail());

      //init assignments
      assignment = Assignment.createAssignment(assignmentNAME, assignmentDESCRIPTION, assignmentDEADLINE,
            assignmentMULTIPLICATOR, assignmentCOMMENT, assignmentADDITIONAL,
            lecture);

      //init stuff
      stuff = Stuff.createStuff(stuffFILENAME, stuffTYPE, assignment);

      //init subtasks
      subtask = Subtask.createSubtask(subtaskNAME, subtaskDESCRIPTION, subtaskPOINTS, subtaskADDITIONAL, assignment);

      //init dutys
      duty = Duty.createDuty(dutyUPLOADEDFILE, dutyUPLOADEDDATETIME, user, assignment);

      //init assessments
      assessment = Assessment.createAssessment(duty);

      //init valuations
      valuation = Valuation.createValuation(assessment, subtask);
   }
}
