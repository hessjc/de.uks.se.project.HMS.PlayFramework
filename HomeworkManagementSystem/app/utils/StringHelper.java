package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Assignment;
import models.Lecture;

public class StringHelper
{
   public static String safeString(String string)
   {
      return string.replace("/", "_").replace(" ", "_");
   }

   public static String[] splitStringWithWhitespaces(String string)
   {
      return string != null ? string.split("\\s+") : null;
   }

   public static Lecture splitUrlSyntaxInLecture(String url)
   {
      try
      {
         String[] value = url.split("/");
         return Lecture.findLectureByID(Integer.valueOf(value[2]));
      }
      catch (Exception e)
      {
         return null;
      }
   }
   
   public static Assignment splitUrlSyntaxInAssignment(String url)
   {
      try
      {
         String[] value = url.split("/");
         return Assignment.findAssignmentByID(Integer.valueOf(value[4]));
      }
      catch (Exception e)
      {
         return null;
      }
   }
}
