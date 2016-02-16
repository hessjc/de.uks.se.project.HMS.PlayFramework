package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import util.InMemoryModel;

public class LectureTest extends InMemoryModel
{

   @Test
   public void checkAttributes()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(lecture.getName());
            assertEquals(lecture.getName(), lectureNAME);
            assertNotNull(lecture.getDescription());
            assertEquals(lecture.getDescription(), lectureDESCRIPTION);
            assertNotNull(lecture.getClosingdate());
            assertEquals(lecture.getClosingdate(), lectureCLOSINGDATE);
            assertNotNull(lecture.getSemester());
            assertEquals(lecture.getSemester(), semester);
         }
      });
   }

   @Test
   public void checkUniqueID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            for (Lecture lecture : Lecture.findAll())
            {
               assertEquals(1, Collections.frequency(Lecture.findLectureIDsAsList(), lecture.getId()));
            }
         }
      });
   }

   @Test
   public void checkFindLectureIDsAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            List<Long> lecture = Lecture.findLectureIDsAsList();
            assertEquals(lecture.size(), Lecture.findAll().size());
         }
      });
   }
}
