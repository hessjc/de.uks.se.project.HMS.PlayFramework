package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;

import util.InMemoryModel;

public class SemesterTest extends InMemoryModel
{

   @Test
   public void checkAnnotations() throws ClassNotFoundException
   {
      assertTrue(semester.getClass().isAnnotationPresent(Entity.class));
      assertTrue(semester.getClass().isAnnotationPresent(Table.class));
   }

   @Test
   public void checkCreatedSemester()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(semester);
            assertTrue(semester.getClass().equals(Semester.class));
         }
      });
   }

   @Test
   public void checkEditSemester()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            Semester.editSemester(semester, "Semestername");
            assertEquals(semester.getSemester(), "Semestername");
         }
      });
   }

   @Test
   public void checkDeleteSemester()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(semester);
            String name = semester.getSemester();
            Semester.deleteSemester(semester);
            assertNull(Semester.findSemester(name));
         }
      });
   }

   @Test
   public void checkAttributes()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(semester.getId());
            assertEquals(semester.getSemester(), semesterNAME);
            assertTrue(semester.getSemester().getClass().equals(String.class));
         }
      });
   }

   @Test
   public void checkBidirectionality()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            lecture.setSemester(semester);
            lecture.update();
            assertTrue(semester.getLectures().contains(lecture));
            assertEquals(lecture.getSemester(), semester);
         }
      });
   }

   @Test
   public void checkAddLecture()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(1, semester.getLectures().size());
            semester.addLecture(Lecture.createLecture("tmpLecture", null, "tmpDescription", semester, lectureadmin.getEmail()));
            assertEquals(2, semester.getLectures().size());
            assertEquals(lecture.getSemester(), semester);
         }
      });
   }

   @Test
   public void checkRemoveLecture()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(1, semester.getLectures().size());
            semester.removeLecture(lecture);
            assertEquals(0, semester.getLectures().size());
            assertNull(lecture.getSemester());
            assertFalse(semester.getLectures().contains(lecture));
         }
      });
   }

   @Test
   public void checkStaticAttributes()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertTrue(Semester.WS_Pattern.getClass().equals(Pattern.class));
            assertTrue(Semester.SS_Pattern.getClass().equals(Pattern.class));
         }
      });
   }

   @Test
   public void checkGetLectures()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            semester.addLecture(lecture);
            assertEquals(1, semester.getLectures().size());
         }
      });
   }

   @Test
   public void checkUniqueID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            for (Semester semester : Semester.findAll())
            {
               assertEquals(1, Collections.frequency(Semester.findSemesterIDsAsList(), semester.getId()));
            }
         }
      });
   }

   @Test
   public void checkUniqueName()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            for (Roles role : Roles.findAll())
            {
               assertEquals(1, Collections.frequency(Roles.findRolesNamesAsList(), role.getName()));
            }
         }
      });
   }

   @Test
   public void checkFindSemester()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(semester, Semester.findSemester(semesterNAME));
         }
      });
   }

   @Test
   public void checkFindSemestersAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            List<String> semesters = Semester.findSemestersAsList();
            assertEquals(semesters.size(), Semester.findAll().size());
         }
      });
   }

   @Test
   public void checkFindSemesterIDsAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            List<Long> semesters = Semester.findSemesterIDsAsList();
            assertEquals(semesters.size(), Semester.findAll().size());
         }
      });
   }

   @Test
   public void checkCompareToAndGetYearMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            int result = othersemester.compareTo(semester);
            assertEquals(0, result < 0 ? 1 : 0);
         }
      });
   }
}
