package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.junit.Test;

import util.InMemoryModel;

public class DutyTest extends InMemoryModel
{

   @Test
   public void checkAnnotations() throws ClassNotFoundException
   {
      assertTrue(duty.getClass().isAnnotationPresent(Entity.class));
      assertTrue(duty.getClass().isAnnotationPresent(Table.class));
   }

   @Test
   public void checkCreatedDuty()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(duty);
            assertEquals(duty, Duty.findDutyByID(duty.getId()));
            assertTrue(duty.getClass().equals(Duty.class));
         }
      });
   }

   @Test
   public void checkEditDuty()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            DateTime dateTime = new DateTime();
            Duty.editDuty(duty, "new/path/to/files", dateTime);
            assertEquals(duty.getUploadedFile(), "new/path/to/files");
            assertEquals(duty.getUploadedDateTime(), dateTime);
         }
      });
   }

   @Test
   public void checkDeleteDuty()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            long id = duty.getId();
            assertNotNull(duty);
            Duty.deleteDuty(duty);
            //assertNull(Duty.findDutyByID(id));
         }
      });
   }

   @Test
   public void checkCatchDuty()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertFalse(duty.isCatched());
            Duty.catchDuty(duty, user);
            assertTrue(duty.isCatched());
            Duty.releaseDuty(duty);
         }
      });
   }

   @Test
   public void checkCloseDuty()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertFalse(duty.isCorrected());
            Duty.closeDuty(duty);
            assertTrue(duty.isCorrected());
         }
      });
   }

   @Test
   public void checkReleaseDuty()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            Duty.catchDuty(duty, user);
            Duty.closeDuty(duty);
            assertTrue(duty.isCatched());
            assertTrue(duty.isCorrected());
            Duty.releaseDuty(duty);
            assertFalse(duty.isCatched());
            assertFalse(duty.isCorrected());
         }
      });
   }

   @Test
   public void checkAttributes()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(duty.getId());
            assertNotNull(duty.getUploadedFile());
            assertEquals(duty.getUploadedFile(), dutyUPLOADEDFILE);
            assertTrue(duty.getUploadedFile().getClass().equals(String.class));
            assertNotNull(duty.getUploadedDateTime());
            assertEquals(duty.getUploadedDateTime(), dutyUPLOADEDDATETIME);
            assertTrue(duty.getUploadedDateTime().getClass().equals(DateTime.class));
            assertNotNull(duty.isCatched());
            assertFalse(duty.isCatched());
            assertNotNull(duty.isCorrected());
            assertFalse(duty.isCorrected());
            assertEquals(user, duty.getUser());
         }
      });
   }

   @Test
   public void findAssignment()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
         }
      });
   }

   @Test
   public void findUser()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
         }
      });
   }

   @Test
   public void checkUniqueName()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            for (Duty duty : Duty.findAll())
            {
               assertEquals(1, Collections.frequency(Duty.findDutyIDsAsList(), duty.getId()));
            }
         }
      });
   }

   @Test
   public void checkFindDutyIDsAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            List<Long> dutys = Duty.findDutyIDsAsList();
            assertEquals(dutys.size(), Duty.findAll().size());
         }
      });
   }

   @Test
   public void checkFindDutyByID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(duty, Duty.findDutyByID(duty.getId()));
         }
      });
   }

   @Test
   public void checkFindDutyByAssessmentID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(duty, Duty.findDutyByAssessmentID(assessment.getId()));
         }
      });
   }

   @Test
   public void checkFindDutysByAssessmentID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(assignment.getDutys().size(), Duty.findDutysByAssignmentID(assignment.getId()).size());
         }
      });
   }

   @Test
   public void checkFindCatchedDutysByAssessmentID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            int counter = 0;
            for (Duty duty : Duty.findDutysByAssignmentID(assignment.getId()))
            {
               if (duty.isCatched()) counter++;
            }
            assertEquals(counter, Duty.findCatchedDutysByAssignmentID(assignment.getId()).size());
         }
      });
   }

   @Test
   public void checkFindNotCatchedDutysByAssessmentID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            int counter = 0;
            for (Duty duty : Duty.findDutysByAssignmentID(assignment.getId()))
            {
               if (!duty.isCatched()) counter++;
            }
            assertEquals(counter, Duty.findNotCatchedDutysByAssignmentID(assignment.getId()).size());
         }
      });
   }

   @Test
   public void checkFindCorrectedDutysByAssessmentID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            int counter = 0;
            for (Duty duty : Duty.findDutysByAssignmentID(assignment.getId()))
            {
               if (duty.isCorrected()) counter++;
            }
            assertEquals(counter, Duty.findCorrectedDutysByAssignmentID(assignment.getId()).size());
         }
      });
   }

   @Test
   public void checkFindDutyByUserAndAssignment()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(duty, Duty.findDutyByUserANDAssignment(user, assignment));
         }
      });
   }

   @Test
   public void checkFindRolesNamesAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            List<String> roles = Roles.findRolesNamesAsList();
            assertEquals(roles.size(), Roles.findAll().size());
         }
      });
   }

   @Test
   public void checkFindUploadedDutysByUserID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(user.getDutys().size(), Duty.findUploadedDutysByUserID(user.getId()).size());
         }
      });
   }
}
