package models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import be.objectify.deadbolt.core.models.Role;

@Entity(name = "roles")
@Table(name = "roles")
public class Roles extends HMSModelElement implements Role
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @NotNull
   public String name;

   @OneToMany(mappedBy = "role")
   public Set<User> users = new LinkedHashSet<User>();

   public static final Finder<Long, Roles> find = new Finder<Long, Roles>(
         Long.class, Roles.class);

   public static List<Roles> findAll()
   {
      return find.where().order().asc("name").findList();
   }

   public static Roles findRoleByName(String name)
   {
      return find.where().eq("name", name).findUnique();
   }
   
   public static List<String> findRolesNamesAsList()
   {
      List<String> roles = new ArrayList<String>();
      for (Role role : Roles.findAll())
      {
         roles.add(role.getName());
      }
      return roles;
   }
   
   public static Roles createRole(String name)
   {
      Roles role = new Roles();
      role.setName(name);
      role.save();
      return role;
   }

   public static void editRole(Roles role, String name)
   {
      role.setName(name);
      role.update();
   }

   public static void deleteRole(Roles role)
   {
      List<User> userList = new ArrayList<User>();
      userList.addAll(role.getUsers());
      for(User user : userList)
      {
         user.setRole(null);
      }
      role.delete();
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public Set<User> getUsers()
   {
      return users;
   }

   public boolean addUser(User user)
   {
      boolean changed = users.add(user);
      if (changed && user != null)
      {
         user.setRole(this);
      }
      return changed;
   }

   public boolean removeUser(User user)
   {
      boolean changed = users.remove(user);
      if (changed && user != null)
      {
         user.setRole(null);
      }
      return changed;
   }
}
