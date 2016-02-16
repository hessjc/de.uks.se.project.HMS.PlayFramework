package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;

import util.HashUtil;
import util.InMemoryModel;

public class UserTest extends InMemoryModel
{
   @Test
   public void checkAnnotations() throws ClassNotFoundException
   {
      assertTrue(user.getClass().isAnnotationPresent(Entity.class));
      assertTrue(user.getClass().isAnnotationPresent(Table.class));
   }

   @Test
   public void checkCreatedUser()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(user);
            assertTrue(user.getClass().equals(User.class));
         }
      });
   }

   @Test
   public void checkCreatedStudent()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(student);
            assertTrue(student.getClass().equals(User.class));
         }
      });
   }

   @Test
   public void checkDeleteUser()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(user);
            User.deleteUser(user);
            assertNull(User.findUser(userEMAIL));
         }
      });
   }

   @Test
   public void checkStudentsAttributes()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(student.getId());
            assertTrue(student.getId().getClass().equals(Long.class));
            assertNotNull(student.getEmail());
            assertEquals(student.getEmail(), userStudentEMAIL);
            assertTrue(student.getEmail().getClass().equals(String.class));
            assertNotNull(student.getFirstName());
            assertEquals(student.getFirstName(), userFIRSTNAME);
            assertTrue(student.getFirstName().getClass().equals(String.class));
            assertNotNull(student.getLastName());
            assertEquals(student.getLastName(), userLASTNAME);
            assertTrue(student.getLastName().getClass().equals(String.class));
            assertNotNull(student.getPassword());
            assertEquals(student.getPassword(), HashUtil.computeSha1(userPASSWORD));
            assertTrue(student.getPassword().getClass().equals(String.class));
            assertNotNull(student.getMatrikelNumber());
            assertEquals(student.getMatrikelNumber(), userStudentMATRIKELNUMBER);
            assertTrue(student.getMatrikelNumber().getClass().equals(String.class));
            assertNull(student.getLastLogin());
            assertFalse(student.isEmailValidated());
         }
      });
   }

   @Test
   public void checkStudentsRole()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(student.getRole());
            assertEquals(student.getRole(), Roles.findRoleByName("Student"));
            assertTrue(student.getRole().getClass().equals(Roles.class));
         }
      });
   }

   @Test
   public void checkUsersRole()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(user.getRole());
            assertEquals(user.getRole(), role);
            assertTrue(user.getRole().getClass().equals(Roles.class));
         }
      });
   }

   @Test
   public void checkMatrikelNumberCanBeNULL()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNull(user.getMatrikelNumber());
         }
      });
   }

   @Test
   public void findLectures()
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
   public void findAssessments()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
         }
      });
   }

   @Test
   public void checkIsLectureadminOf()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            //TODO
         }
      });
   }

   @Test
   public void checkIsProofreaderOf()
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
            for (User user : User.findAll())
            {
               assertEquals(1, Collections.frequency(User.findUsersIDsAsList(), user.getId()));
            }
         }
      });
   }

   @Test
   public void checkUniqueEmail()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            for (User user : User.findAll())
            {
               assertEquals(1, Collections.frequency(User.findUsersEmailsAsList(), user.getEmail()));
            }
         }
      });
   }

   @Test
   public void checkUniqueMatrikelNumber()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            for (User user : User.findAll())
            {
               if (user.getMatrikelNumber() != null)
               {
                  assertEquals(1,
                        Collections.frequency(User.findUsersMatrikelNumbersAsList(), user.getMatrikelNumber()));
               }
            }
         }
      });
   }

   @Test
   public void checkFindUserByID()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(User.findUserByID(user.getId()));
         }
      });
   }

   @Test
   public void checkFindUserByEmail()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(User.findUserByEmail(userEMAIL));
         }
      });
   }

   @Test
   public void checkFindStudentByMatrikelNumber()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(User.findUserByMatrikelNumber(userStudentMATRIKELNUMBER));
         }
      });
   }

   @Test
   public void checkFindUserMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            User testStudent1 = User.findUser(String.valueOf(student.getId()));
            User testStudent2 = User.findUser(userStudentEMAIL);
            User testStudent3 = User.findUser(userStudentMATRIKELNUMBER);
            assertNotNull(testStudent1);
            assertNotNull(testStudent2);
            assertNotNull(testStudent3);
            assertEquals(testStudent1, testStudent2);
            assertEquals(testStudent2, testStudent3);
         }
      });
   }

   @Test
   public void checkFindUsersIDsAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            List<Long> users = User.findUsersIDsAsList();
            assertEquals(users.size(), User.findAll().size());
         }
      });
   }

   @Test
   public void checkFindUsersEmailsAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            List<String> users = User.findUsersEmailsAsList();
            assertEquals(users.size(), User.findAll().size());
         }
      });
   }

   @Test
   public void checkFindUsersMatrikelNumbersAsListMethod()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            List<String> users = User.findUsersMatrikelNumbersAsList();
            assertEquals(users.size(), User.findAll().size());
         }
      });
   }
}
