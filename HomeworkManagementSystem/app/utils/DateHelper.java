package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

public class DateHelper
{

   final static String DATE_FORMAT = "dd-MM-yyyy";
   final static DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

   public static Date currentTime()
   {
      return new Date();
   }

   public static String formatDateToString(Date date)
   {
      SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
      return df.format(date);
   }

   public static boolean isDateValid(String date)
   {
      try
      {
         DateFormat df = new SimpleDateFormat(DATE_FORMAT);
         df.setLenient(false);
         df.parse(date);
         return true;
      }
      catch (ParseException e)
      {
         return false;
      }
   }

   public static boolean isDateValid(DateTime date)
   {
      // TODO just a workaround, the check should be better here
      return date == null ? false : isDateValid(date.toDate());
   }

   public static boolean isDateValid(Date date)
   {
      Calendar cal = Calendar.getInstance();
      try
      {
         cal.setTime(date);
         return true;
      }
      catch (Exception e)
      {
         return false;
      }
   }

   public static boolean isExpired(DateTime date)
   {
      return date != null && date.isBeforeNow();
   }
}
