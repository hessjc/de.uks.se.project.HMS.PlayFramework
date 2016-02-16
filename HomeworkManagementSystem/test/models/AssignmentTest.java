package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.joda.time.DateTime;
import org.junit.Test;

import util.InMemoryModel;

public class AssignmentTest extends InMemoryModel
{

   @Test
   public void checkCreateAssignment()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(assignment);
            assertEquals(assignment, Assignment.findAssignmentByID(assignment.getId()));
            assertTrue(assignment.getClass().equals(Assignment.class));
         }
      });
   }

   @Test
   public void checkEditAssignment()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            DateTime date = new DateTime();
            Assignment.editAssignment(assignment, "NeuerName", "NeueBeschreibung", date, 2, "NeuerKommentar", true);
            assertEquals(assignment.getName(), "NeuerName");
            assertNotEquals(assignment.getName(), assignmentNAME);
            assertEquals(assignment.getDescription(), "NeueBeschreibung");
            assertNotEquals(assignment.getDescription(), assignmentDESCRIPTION);
            assertEquals(assignment.getDeadline(), date);
            assertNotEquals(assignment.getDeadline(), assignmentDEADLINE);
            assertEquals(assignment.getMultiplicator(), 2);
            assertNotEquals(assignment.getMultiplicator(), assignmentMULTIPLICATOR);
            assertEquals(assignment.getComment(), "NeuerKommentar");
            assertNotEquals(assignment.getComment(), assignmentCOMMENT);
            assertEquals(assignment.isAdditional(), true);
            assertNotEquals(assignment.isAdditional(), assignmentADDITIONAL);
            Assignment.editAssignment(assignment, assignmentNAME, assignmentDESCRIPTION, assignmentDEADLINE,
                  assignmentMULTIPLICATOR, assignmentCOMMENT, false);
         }
      });
   }

   @Test
   public void checkDeleteAssignment()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(assignment);
            long id = assignment.getId();
            Assignment.deleteAssignment(assignment);
            assertNull(Assignment.findAssignmentByID(id));
         }
      });
   }

   @Test
   public void checkAttributes()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(assignment.getId());
            assertNotNull(assignment.getName());
            assertEquals(assignment.getName(), assignmentNAME);
            assertTrue(assignment.getName().getClass().equals(String.class));
            assertNotNull(assignment.getDescription());
            assertEquals(assignment.getDescription(), assignmentDESCRIPTION);
            assertTrue(assignment.getDescription().getClass().equals(String.class));
            assertNotNull(assignment.getDeadline());
            assertEquals(assignment.getDeadline(), assignmentDEADLINE);
            assertTrue(assignment.getDeadline().getClass().equals(org.joda.time.DateTime.class));
            assertNotNull(assignment.getMultiplicator());
            assertEquals(assignment.getMultiplicator(), assignmentMULTIPLICATOR);
            assertNotNull(assignment.getComment());
            assertEquals(assignment.getComment(), assignmentCOMMENT);
            assertTrue(assignment.getComment().getClass().equals(String.class));
            assertNotNull(assignment.isAdditional());
            assertEquals(assignment.isAdditional(), assignmentADDITIONAL);
            assertNotNull(assignment.isCompleted());
            assertEquals(assignment.isCompleted(), false);
         }
      });
   }
   
   @Test
   public void findStuff()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
         }
      });
   }
   
   @Test
   public void findLecture()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
         }
      });
   }
   
   @Test
   public void findDutys()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
         }
      });
   }
   
   @Test
   public void findSubtasks()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
         }
      });
   }

   @Test
   public void checkUniqueID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            for (Assignment assignment : Assignment.findAll())
            {
               assertEquals(1, Collections.frequency(Assignment.findAssignmentsIDsAsList(), assignment.getId()));
            }
         }
      });
   }

   @Test
   public void checkFindAssignmentByID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(assignment, Assignment.findAssignmentByID(assignment.getId()));
         }
      });
   }

   @Test
   public void checkFindAssignmentByDutyID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(assignment, Assignment.findAssignmentByDutyID(duty.getId()));
         }
      });
   }

   @Test
   public void checkFindAssignmentsByLectureID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
//            assertEquals(lecture.getAssignments().size(), Assignment.findAssignmentsByLectureID(lecture.getId()).size());
         }
      });
   }

   @Test
   public void checkFindAssignmentsIDsAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(Assignment.findAll().size(), Assignment.findAssignmentsIDsAsList().size());
         }
      });
   }
   
   @Test
   public void checkPointsByAssignmentID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
//            double result = 0;
//            for(Subtask subtask : assignment.getSubtasks()) {
//               result = result + subtask.getPoints();
//            }
//            assertEquals(result, Assignment.pointsByAssignmentID(assignment.getId()), 0.0001);
         }
      });
   }
}
