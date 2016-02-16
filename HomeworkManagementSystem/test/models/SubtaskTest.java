package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;

import util.InMemoryModel;

public class SubtaskTest extends InMemoryModel
{

   @Test
   public void checkAnnotations() throws ClassNotFoundException
   {
      assertTrue(subtask.getClass().isAnnotationPresent(Entity.class));
      assertTrue(subtask.getClass().isAnnotationPresent(Table.class));
   }
   
   @Test
   public void checkCreatedSubtask()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(subtask);
            assertEquals(subtask, Subtask.findSubtaskByID(subtask.getId()));
            assertTrue(subtask.getClass().equals(Subtask.class));
         }
      });
   }

   @Test
   public void checkEditSubtask()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            Assignment tmpAssignment = Assignment.createAssignment(assignmentNAME, assignmentDESCRIPTION, assignmentDEADLINE, assignmentMULTIPLICATOR, assignmentCOMMENT, assignmentADDITIONAL, lecture);
            Subtask.editSubtask(subtask, "NeuerName", "NeueBeschreibung", 99.55f, true, tmpAssignment);
            assertEquals(subtask.getName(), "NeuerName");
            assertEquals(subtask.getDescription(), "NeueBeschreibung");
            assertEquals(subtask.getPoints(), 99.55f, 0.0001);
            assertEquals(subtask.isAdditional(), true);
            assertEquals(subtask.getAssignment(), tmpAssignment);
         }
      });
   }

   @Test
   public void checkDeleteSubtask()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(subtask);
            long id = subtask.getId();
            Subtask.deleteSubtask(subtask);
            assertNull(Subtask.findSubtaskByID(id));
         }
      });
   }

   @Test
   public void checkAttributes()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(subtask.getId());
            assertNotNull(subtask.getName());
            assertEquals(subtask.getName(), subtaskNAME);
            assertTrue(subtask.getName().getClass().equals(String.class));
            assertNotNull(subtask.getDescription());
            assertEquals(subtask.getDescription(), subtaskDESCRIPTION);
            assertTrue(subtask.getDescription().getClass().equals(String.class));
            assertNotNull(subtask.getPoints());
            assertEquals(subtask.getPoints(), subtaskPOINTS, 0.0001);
            assertNotNull(subtask.isAdditional());
            assertEquals(subtask.isAdditional(), subtaskADDITIONAL);
         }
      });
   }
   
   @Test
   public void findAssignment()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(subtask.getAssignment());
         }
      });
   }
   
   @Test
   public void findValuations()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(subtask.getValuations());
         }
      });
   }
   
   @Test
   public void checkBidirectionality() {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(subtask.getAssignment(), assignment);
            for(Valuation valuation : subtask.getValuations()) {
               assertEquals(valuation.getSubtask(), subtask);
            }
         }
      });
   }
   
   @Test
   public void checkAddValuation() {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            int count = subtask.getValuations().size();
            Valuation tmpValuation = Valuation.createValuation(assessment, subtask);
            subtask.addValuation(tmpValuation);
            assertTrue(subtask.getValuations().contains(tmpValuation));
            assertEquals(++count, subtask.getValuations().size());
         }
      });
   }
   
   @Test
   public void checkRemoveValuation() {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            subtask.addValuation(valuation);
            int count = subtask.getValuations().size();
            subtask.removeValuation(valuation);
            assertFalse(subtask.getValuations().contains(valuation));
            assertEquals(count - 1, subtask.getValuations().size());
         }
      });
   }
   
   @Test
   public void checkUniqueIDs()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            for (Subtask subtask : Subtask.findAll())
            {
               assertEquals(1, Collections.frequency(Subtask.findSubtasksIDsAsList(), subtask.getId()));
            }
         }
      });
   }

   @Test
   public void checkFindSubtaskByID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(subtask, Subtask.findSubtaskByID(subtask.getId()));
         }
      });
   }

   @Test
   public void checkFindSubtasksByAssignmentID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(assignment.getSubtasks().size(), Subtask.findSubtasksByAssignmentID(subtask.getAssignment().getId()).size());
         }
      });
   }

   @Test
   public void checkPointsByID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(subtask.getPoints(), Subtask.pointsByID(subtask.getId()), 0.0001);
         }
      });
   }
}
