package models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "subtasks")
@Table(name = "subtasks")
public class Subtask extends HMSModelElement
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @NotNull
   public String name;
   @Column(length = 65535, columnDefinition = "Text")
   @NotNull
   public String description;

   @NotNull
   public float points;

   public boolean additional = false;

   @ManyToOne
   public Assignment assignment;

   @OneToMany(mappedBy = "subtask")
   public Set<Valuation> valuations = new LinkedHashSet<Valuation>();

   public static final Finder<Long, Subtask> find = new Finder<Long, Subtask>(
         Long.class, Subtask.class);

   public static List<Subtask> findAll()
   {
      return find.all();
   }

   public static Subtask findSubtaskByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }

   public static List<Subtask> findSubtasksByAssignmentID(long aid)
   {
      return find.where().eq("assignment_id", aid).findList();
   }

   public static Subtask createSubtask(String name, String description, float points, boolean additional,
         Assignment assignment)
   {
      Subtask subtask = new Subtask();
      subtask.setName(name);
      subtask.setDescription(description);
      subtask.setPoints(points);
      subtask.save();
      subtask.setAdditional(additional);
      subtask.update();
      subtask.setAssignment(assignment);
      return subtask;
   }

   public static void editSubtask(Subtask subtask, String name, String description, float points, boolean additional,
         Assignment assignment)
   {
      subtask.setName(name);
      subtask.setDescription(description);
      subtask.setPoints(points);
      subtask.setAdditional(additional);
      subtask.update();
      subtask.setAssignment(assignment);
   }

   public static void deleteSubtask(Subtask subtask)
   {
      ArrayList<Valuation> valuationList = new ArrayList<Valuation>();
      valuationList.addAll(subtask.getValuations());
      for (Valuation valuation : valuationList)
      {
         Valuation.deleteValuation(valuation);
      }
      subtask.setAssignment(null);
      subtask.delete();
   }

   public static List<Long> findSubtasksIDsAsList()
   {
      List<Long> subtasks = new ArrayList<Long>();
      for (Subtask subtask : Subtask.findAll())
      {
         subtasks.add(subtask.getId());
      }
      return subtasks;
   }

   public long getId()
   {
      return id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public float getPoints()
   {
      return points;
   }

   public void setPoints(float points)
   {
      this.points = points;
   }

   public boolean isAdditional()
   {
      return additional;
   }

   public void setAdditional(boolean additional)
   {
      this.additional = additional;
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
            oldAssignment.removeSubtask(this);
         }
         if (assignment != null)
         {
            assignment.addSubtask(this);
         }
         this.update();
      }
   }

   public Set<Valuation> getValuations()
   {
      return valuations;
   }

   public boolean addValuation(Valuation valuation)
   {
      boolean changed = valuations.add(valuation);
      if (changed && valuation != null)
      {
         valuation.setSubtask(this);
      }
      return changed;
   }

   public boolean removeValuation(Valuation valuation)
   {
      boolean changed = valuations.remove(valuation);
      if (changed && valuation != null)
      {
         valuation.setAssessment(null);
      }
      return changed;
   }

   public static float pointsByID(long idS)
   {
      Subtask subtask = findSubtaskByID(idS);
      if (subtask == null)
      {
         return -1;
      }
      else
      {
         return subtask.points;
      }
   }
}
