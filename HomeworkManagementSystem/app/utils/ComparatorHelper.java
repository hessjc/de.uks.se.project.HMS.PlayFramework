package utils;

import java.util.Comparator;

import models.Duty;
import models.User;

public class ComparatorHelper
{

   public final static Comparator<User> userComparator = new Comparator<User>() {

      @Override
      public int compare(User duty1, User duty2)
      {
         if (duty1.getLastName().compareTo(duty2.getLastName())== 0)
         {
            return duty1.getFirstName().compareTo(duty2.getFirstName());
         }
         else
         {
            return duty1.getLastName().compareTo(duty2.getLastName());
         }
      };
   };
   
   public final static Comparator<Duty> dutyComparator = new Comparator<Duty>() {

      @Override
      public int compare(Duty duty1, Duty duty2)
      {
         if (duty1.getUser().getLastName().compareTo(duty2.getUser().getLastName())== 0)
         {
            return duty1.getUser().getFirstName().compareTo(duty2.getUser().getFirstName());
         }
         else
         {
            return duty1.getUser().getLastName().compareTo(duty2.getUser().getLastName());
         }
      };
   };
}
