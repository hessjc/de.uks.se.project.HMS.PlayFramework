/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package security;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Lecture;
import models.Roles;
import models.User;
import play.Logger;
import play.mvc.Http;
import utils.PlayHelper;
import be.objectify.deadbolt.java.AbstractDynamicResourceHandler;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;

public class HDSDynamicResourceHandler implements DynamicResourceHandler
{
   private static final Map<String, DynamicResourceHandler> HANDLERS = new HashMap<String, DynamicResourceHandler>();

   public static final String ADMINISTRATOR = "Administrator";
   public static final String IS_LECTUREADMIN_OF_LECTURE = "IsLectureadminOfLecture";
   
   static
   {
      HANDLERS.put(ADMINISTRATOR, new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.getRole().getName().equals(ADMINISTRATOR))
               {
                  return true;
               }
            }
            return false;
         }
      });

      HANDLERS.put("Lectureadmin", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.isLectureadminOf().size() != 0 || ADMINISTRATOR.equals(user.getRole().getName()))
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            return false;
         }
      });

      HANDLERS.put("Proofreader", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.isProofreaderOf().size() != 0 || user.isLectureadminOf().size() != 0
                     || ADMINISTRATOR.equals(user.getRole().getName()))
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            return false;
         }
      });

      HANDLERS.put("Student", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.getRole().getName().equals("Student") || ADMINISTRATOR.equals(user.getRole().getName()))
               {
                  return true;
               }
            }
            return false;
         }
      });

      HANDLERS.put("Participant", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.getRole().getName().equals("Student") || ADMINISTRATOR.equals(user.getRole().getName()))
               {
                  return true;
               }
               else
               {
                  if (user.isProofreaderOf().size() != 0 || user.isLectureadminOf().size() != 0)
                  {
                     return true;
                  }
               }
            }
            return false;
         }
      });

      HANDLERS.put("User", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               Roles role = user.getRole();
               if (role != null && role.getName().equals("User")
                     || ADMINISTRATOR.equals(role.getName())
                     || role.getName().equals("Student"))
               {
                  return true;
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsUserAndNoProofReaderOrLectureAdmin", new AbstractDynamicResourceHandler() {
         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null && user.getRole().getName().equals("User"))
            {
               String path = context.request().path();
               if (path != null)
               {
                  Pattern p = Pattern.compile(PlayHelper.context() + "lecture/(\\d+)/.*");
                  Matcher matcher = p.matcher(path);
                  if (matcher.matches())
                  {
                     long lectureId = Long.valueOf(matcher.group(1)).longValue();
                     Lecture lecture = Lecture.findLectureByID(lectureId);
                     if (lecture != null)
                     {
                        return !lecture.getProofreaders().contains(user) && !lecture.getLectureadmins().contains(user);
                     }
                  }
               }
            }
            return false;
         }
      });

      HANDLERS.put(IS_LECTUREADMIN_OF_LECTURE, new AbstractDynamicResourceHandler()
      {
         public boolean isAllowed(String name,
               String meta,
               DeadboltHandler deadboltHandler,
               Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               String path = context.request().path();

               if (path != null)
               {
                  Pattern p = Pattern.compile(PlayHelper.context() + "lecture/(\\d+)/.*");
                  Matcher matcher = p.matcher(path);
                  if (matcher.matches())
                  {
                     long lectureId = Long.valueOf(matcher.group(1)).longValue();
                     Lecture lecture = Lecture.findLectureByID(lectureId);
                     if (lecture != null)
                     {
                        if (lecture.getLectureadmins().contains(user))
                        {
                           return true;
                        }
                        else
                        {
                           if (ADMINISTRATOR.equals(user.getRole().getName()))
                           {
                              return true;
                           }
                           return false;
                        }
                     }
                  }
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsProofreaderOfLecture", new AbstractDynamicResourceHandler()
      {
         public boolean isAllowed(String name,
               String meta,
               DeadboltHandler deadboltHandler,
               Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               String path = context.request().path();
               if (path != null)
               {
                  Pattern p = Pattern.compile(PlayHelper.context() + "lecture/(\\d+)/.*");
                  Matcher matcher = p.matcher(path);
                  if (matcher.matches())
                  {
                     long lectureId = Long.valueOf(matcher.group(1)).longValue();
                     Lecture lecture = Lecture.findLectureByID(lectureId);
                     if (lecture != null)
                     {
                        if (lecture.getProofreaders().contains(user) || lecture.getLectureadmins().contains(user))
                        {
                           return true;
                        }
                        else
                        {
                           if (ADMINISTRATOR.equals(user.getRole().getName()))
                           {
                              return true;
                           }
                           return false;
                        }
                     }
                  }
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsProofreaderOfLectureOnly", new AbstractDynamicResourceHandler()
      {
         public boolean isAllowed(String name,
               String meta,
               DeadboltHandler deadboltHandler,
               Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               String path = context.request().path();
               if (path != null)
               {
                  Pattern p = Pattern.compile(PlayHelper.context() + "lecture/(\\d+)/.*");
                  Matcher matcher = p.matcher(path);
                  if (matcher.matches())
                  {
                     long lectureId = Long.valueOf(matcher.group(1)).longValue();
                     Lecture lecture = Lecture.findLectureByID(lectureId);
                     if (lecture != null)
                     {
                        if (lecture.getProofreaders().contains(user))
                        {
                           return true;
                        }
                        else
                        {
                           return false;
                        }
                     }
                  }
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsParticipantOfLecture", new AbstractDynamicResourceHandler()
      {
         public boolean isAllowed(String name,
               String meta,
               DeadboltHandler deadboltHandler,
               Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               String path = context.request().path();
               if (path != null)
               {
                  Pattern p = Pattern.compile(PlayHelper.context() + "lecture/(\\d+)/.*");
                  Matcher matcher = p.matcher(path);
                  if (matcher.matches())
                  {
                     long lectureId = Long.valueOf(matcher.group(1)).longValue();
                     Lecture lecture = Lecture.findLectureByID(lectureId);
                     if (lecture != null)
                     {
                        if (lecture.getUsers().contains(user) || lecture.getProofreaders().contains(user)
                              || lecture.getLectureadmins().contains(user))
                        {
                           return true;
                        }
                        else
                        {
                           if (ADMINISTRATOR.equals(user.getRole().getName()))
                           {
                              return true;
                           }
                           return false;
                        }
                     }
                  }
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsParticipantOfLectureOnly", new AbstractDynamicResourceHandler()
      {
         public boolean isAllowed(String name,
               String meta,
               DeadboltHandler deadboltHandler,
               Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               String path = context.request().path();
               if (path != null)
               {
                  Pattern p = Pattern.compile(PlayHelper.context() + "lecture/(\\d+)/.*");
                  Matcher matcher = p.matcher(path);
                  if (matcher.matches())
                  {
                     long lectureId = Long.valueOf(matcher.group(1)).longValue();
                     Lecture lecture = Lecture.findLectureByID(lectureId);
                     if (lecture != null)
                     {
                        if (lecture.getUsers().contains(user))
                        {
                           return true;
                        }
                        else
                        {
                           return false;
                        }
                     }
                  }
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsNotParticipantOfLecture", new AbstractDynamicResourceHandler()
      {
         public boolean isAllowed(String name,
               String meta,
               DeadboltHandler deadboltHandler,
               Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               String path = context.request().path();
               if (path != null)
               {
                  Pattern p = Pattern.compile(PlayHelper.context() + "lecture/(\\d+)/.*");
                  Matcher matcher = p.matcher(path);
                  if (matcher.matches())
                  {
                     long lectureId = Long.valueOf(matcher.group(1)).longValue();
                     Lecture lecture = Lecture.findLectureByID(lectureId);
                     if (lecture != null)
                     {
                        if (!lecture.getUsers().contains(user) && !lecture.getProofreaders().contains(user)
                              && !lecture.getLectureadmins().contains(user) && user.getRole().equals(Roles.findRoleByName("Student")))
                        {
                           return true;
                        }
                        else
                        {
                           return false;
                        }
                     }
                  }
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsLectureadmin", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.isLectureadminOf().size() != 0)
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsProofreader", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.isProofreaderOf().size() != 0)
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsParticipant", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.getLectures().size() != 0)
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            return false;
         }
      });

      HANDLERS.put("IsUser", new AbstractDynamicResourceHandler() {

         public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               if (user.getRole().getName().equals("User"))
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            return false;
         }
      });

      HANDLERS.put("PersonalInformation", new AbstractDynamicResourceHandler()
      {
         public boolean isAllowed(String name,
               String meta,
               DeadboltHandler deadboltHandler,
               Http.Context context)
         {
            User user = (User)deadboltHandler.getSubject(context);
            if (user != null)
            {
               String path = context.request().path();
               if (path != null)
               {
                  Pattern p = Pattern.compile(PlayHelper.context() + "account/(\\d+)/.*");
                  Matcher matcher = p.matcher(path);
                  if (matcher.matches())
                  {
                     long userID = Long.valueOf(matcher.group(1)).longValue();

                     if (user.getId() == userID)
                     {
                        return true;
                     }
                     else
                     {
                        return false;
                     }
                  }
               }
            }
            return false;
         }
      });
   }

   public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context)
   {
      DynamicResourceHandler handler = HANDLERS.get(name);
      boolean result = false;
      if (handler == null)
      {
         Logger.error("No handler available for " + name);
      }
      else
      {
         result = handler.isAllowed(name,
               meta,
               deadboltHandler,
               context);
      }
      return result;
   }

   public boolean checkPermission(String permissionValue,
         DeadboltHandler deadboltHandler,
         Http.Context ctx)
   {
      // We don't need permissions
      return false;
   }
}
