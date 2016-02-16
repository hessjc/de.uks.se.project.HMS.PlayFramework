package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;

import util.InMemoryModel;

public class StuffTest extends InMemoryModel
{

   @Test
   public void checkAnnotations() throws ClassNotFoundException
   {
      assertTrue(stuff.getClass().isAnnotationPresent(Entity.class));
      assertTrue(stuff.getClass().isAnnotationPresent(Table.class));
   }

   @Test
   public void checkCreatedStuff()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(role);
            assertEquals(role, Roles.findRoleByName(roleNAME));
            assertTrue(role.getClass().equals(Roles.class));
         }
      });
   }

   @Test
   public void checkEditRole()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(role.getName(), roleNAME);
            Roles.editRole(role, "Neuer Name");
            assertEquals(role.getName(), "Neuer Name");
         }
      });
   }

   @Test
   public void checkDeleteRole()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(role);
            Roles.deleteRole(role);
            assertNull(Roles.findRoleByName(roleNAME));
         }
      });
   }

   @Test
   public void checkAttributes()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertNotNull(role.getName());
            assertEquals(role.getName(), roleNAME);
            assertTrue(role.getName().getClass().equals(String.class));
         }
      });
   }

   @Test
   public void checkBidirectionality()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertTrue(role.getUsers().contains(user));
            assertEquals(user.getRole(), role);
         }
      });
   }

   @Test
   public void checkAddUser()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            int count = 0;
            for (User user : User.findAll())
            {
               if (user.getRole().equals(role)) count++;
            }
            assertEquals(count, role.getUsers().size());
            assertTrue(role.addUser(student));
            assertEquals(++count, role.getUsers().size());
         }
      });
   }

   @Test
   public void checkRemoveUser()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            int count = 0;
            for (User user : User.findAll())
            {
               if (user.getRole().equals(role)) count++;
            }
            assertEquals(count, role.getUsers().size());
            assertTrue(role.removeUser(user));
            assertEquals(--count, role.getUsers().size());
         }
      });
   }

   @Test
   public void checkGetUsers()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            int count = 0;
            for (User user : User.findAll())
            {
               if (user.getRole().equals(role)) count++;
            }
            assertEquals(count, role.getUsers().size());
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
   public void checkFindRoleNyName()
   {
      running(fakeApplication, new Runnable() {
         public void run()
         {
            assertEquals(role, Roles.findRoleByName(roleNAME));
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
}
