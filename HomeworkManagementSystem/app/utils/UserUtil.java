package utils;

import models.Lecture;
import models.User;
import play.Play;
import play.mvc.Http.Context;
import provider.SimpleUserPassAuthProvider;
import security.DeadboltHandler;
import security.HDSDynamicResourceHandler;
import util.HashUtil;
import util.UserFactory;
import authentication.LDAPAuthUserFactory;

public class UserUtil
{
   public static void changePassword(User user, String newPassword)
   {
      //update the session, because the session identifier may be changed here
      user.setPassword(HashUtil.computeSha1(newPassword));
      user.update();

      // TODO this is a hack to update the password in the ldap
      SimpleUserPassAuthProvider authProvider = Play.application().plugin(SimpleUserPassAuthProvider.class);
      if (authProvider != null)
      {
         UserFactory userFactory = authProvider.getUserFactory();
         if (userFactory instanceof LDAPAuthUserFactory)
         {
            ((LDAPAuthUserFactory)userFactory).updatePassword(user.getEmail(), user.getPassword());
         }
      }
   }

   public static boolean isAllowed(final Context context, final String roleName)
   {
      DeadboltHandler deadboltHandler = DeadboltHandler.get();
      return deadboltHandler.getDynamicResourceHandler(context).isAllowed(roleName, null, deadboltHandler, context);
   }

   public static boolean isLectureAdminOfLecture(final Lecture lecture, final User user)
   {
      if (HDSDynamicResourceHandler.ADMINISTRATOR.equals(user.getRole().getName()) || lecture.getLectureadmins().contains(user))
      {
         return true;
      }
      return false;
   }
}
