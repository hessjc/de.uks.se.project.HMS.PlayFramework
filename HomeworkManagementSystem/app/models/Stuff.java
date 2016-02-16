package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "stuff")
@Table(name = "stuff")
public class Stuff extends HMSModelElement
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @NotNull
   public String fileName;

   public String type;

   @ManyToOne
   public Assignment assignment;

   public static final Finder<Long, Stuff> find = new Finder<Long, Stuff>(
         Long.class, Stuff.class);

   public static Stuff findStuffByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }

   public static List<Stuff> findStuffByAssignmentID(long id)
   {
      return find.where().eq("assignment_id", id).findList();
   }

   public static Stuff createStuff(String fileName, String type, Assignment assignment)
   {
      Stuff stuff = new Stuff();
      stuff.setFileName(fileName);
      stuff.setType(type);
      stuff.save();
      stuff.setAssignment(assignment);
      return stuff;
   }

   public static void editStuff(Stuff stuff, String fileName, String type, Assignment assignment)
   {
      stuff.setFileName(fileName);
      stuff.setType(type);
      stuff.update();
      stuff.setAssignment(assignment);
   }

   public static void deleteStuff(Stuff stuff)
   {
      stuff.setAssignment(null);
      stuff.delete();
   }

   public long getId()
   {
      return id;
   }

   public String getFileName()
   {
      return fileName;
   }

   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public Assignment getAssignment()
   {
      return assignment;
   }

   public void setAssignment(Assignment assignment)
   {
      if (this.assignment != assignment)
      {
         Assignment oldAssignment = this.assignment;
         this.assignment = assignment;
         if (oldAssignment != null)
         {
            oldAssignment.removeStuff(this);
         }
         if (assignment != null)
         {
            assignment.addStuff(this);
         }
         this.update();
      }
   }

}
