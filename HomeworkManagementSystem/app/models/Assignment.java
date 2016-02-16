package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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

import play.data.format.Formats.DateTime;
import play.data.validation.Constraints;

@Entity(name = "assignments")
@Table(name = "assignments")
public class Assignment extends HMSModelElement
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @NotNull
   public String name;
   @Column(length = 65535, columnDefinition = "Text")
   @NotNull
   public String description;

   @DateTime(pattern = "dd.MM.yyyy hh:mm:ss")
   public org.joda.time.DateTime deadline;

   @Constraints.Min(1)
   public int multiplicator;

   public String comment;

   public boolean additional = false;

   public boolean completed = false;

   @OneToMany(mappedBy = "assignment")
   public Set<Stuff> stuff = new LinkedHashSet<Stuff>();

   @ManyToOne
   public Lecture lecture;

   @OneToMany(mappedBy = "assignment")
   public Set<Duty> dutys = new LinkedHashSet<Duty>();

   @OneToMany(mappedBy = "assignment")
   public Set<Message> messages = new LinkedHashSet<Message>();

   @OneToMany(mappedBy = "assignment")
   public Set<Subtask> subtasks = new LinkedHashSet<Subtask>();

   public static final Finder<Long, Assignment> find = new Finder<Long, Assignment>(
         Long.class, Assignment.class);

   public static List<Assignment> findAll()
   {
      return find.all();
   }

   public static Assignment findAssignmentByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }

   public static Assignment findAssignmentByDutyID(long id)
   {
      Duty duty = Duty.findDutyByID(id);
      return duty.getAssignment();
   }

   public static List<Assignment> findAssignmentsByLectureID(long id)
   {
      return find.where().eq("lecture_id", id).findList();
   }

   public static Assignment createAssignment(String name, String description,
         org.joda.time.DateTime deadline, int multiplicator, String comment,
         boolean additional, Lecture lecture)
   {
      Assignment assignment = new Assignment();
      assignment.setName(name);
      assignment.setDescription(description);
      assignment.save();
      assignment.setDeadline(deadline);
      assignment.setMultiplicator(multiplicator);
      assignment.setComment(comment);
      assignment.setAdditional(additional);
      assignment.update();
      assignment.setLecture(lecture);
      return assignment;
   }

   public static void editAssignment(Assignment assignment, String name, String description,
         org.joda.time.DateTime deadline, int multiplicator, String comment, boolean additional)
   {
      assignment.setName(name);
      assignment.setDescription(description);
      assignment.setDeadline(deadline);
      assignment.setMultiplicator(multiplicator);
      assignment.setComment(comment);
      assignment.setAdditional(additional);
      assignment.update();
   }

   public static void deleteAssignment(Assignment assignment)
   {
      List<Stuff> stuffList = new ArrayList<Stuff>();
      stuffList.addAll(assignment.getStuff());
      for (Stuff stuff : stuffList)
      {
         Stuff.deleteStuff(stuff);
      }

      List<Subtask> subtaskList = new ArrayList<Subtask>();
      subtaskList.addAll(assignment.getSubtasks());
      for (Subtask subtask : subtaskList)
      {
         Subtask.deleteSubtask(subtask);
      }

      List<Duty> dutyList = new ArrayList<Duty>();
      dutyList.addAll(assignment.getDutys());
      for (Duty duty : dutyList)
      {
         Duty.deleteDuty(duty);
      }

      assignment.setLecture(null);
      assignment.delete();
   }

   public static List<Long> findAssignmentsIDsAsList()
   {
      List<Long> assignments = new ArrayList<Long>();
      for (Assignment assignment : Assignment.findAll())
      {
         assignments.add(assignment.getId());
      }
      return assignments;
   }

   public static float pointsByAssignmentID(long idA)
   {
      float result = 0;
      for (Subtask subtask : Subtask.findSubtasksByAssignmentID(idA))
      {
         if (!subtask.isAdditional())
         {
            result = result + subtask.getPoints();
         }
      }
      return result;
   }

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
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

   public org.joda.time.DateTime getDeadline()
   {
      return deadline;
   }

   public void setDeadline(org.joda.time.DateTime deadline)
   {
      this.deadline = deadline;
   }

   public int getMultiplicator()
   {
      return multiplicator;
   }

   public void setMultiplicator(int multiplicator)
   {
      this.multiplicator = multiplicator;
   }

   public String getComment()
   {
      return comment;
   }

   public void setComment(String comment)
   {
      this.comment = comment;
   }

   public boolean isAdditional()
   {
      return additional;
   }

   public void setAdditional(boolean additional)
   {
      this.additional = additional;
   }

   public boolean isCompleted()
   {
      return completed;
   }

   public void setCompleted(boolean completed)
   {
      this.completed = completed;
      this.update();
   }

   public Set<Stuff> getStuff()
   {
      return stuff;
   }

   public void setStuff(Set<Stuff> stuff)
   {
      this.stuff = stuff;
   }

   public boolean addStuff(Stuff stuff)
   {
      boolean changed = this.stuff.add(stuff);
      if (changed && stuff != null)
      {
         stuff.setAssignment(this);
      }
      return changed;
   }

   public boolean removeStuff(Stuff stuff)
   {
      boolean changed = this.stuff.remove(stuff);
      if (changed && stuff != null)
      {
         stuff.setAssignment(null);
      }
      return changed;
   }

   public Lecture getLecture()
   {
      return lecture;
   }

   public void setLecture(Lecture lecture)
   {
      if (this.lecture != lecture)
      {
         Lecture oldLecture = this.lecture;
         this.lecture = lecture;
         if (oldLecture != null)
         {
            oldLecture.removeAssignment(this);
         }
         if (lecture != null)
         {
            lecture.addAssignment(this);
         }
         this.update();
      }
   }

   public Set<Duty> getDutys()
   {
      return dutys;
   }

   public void setDutys(Set<Duty> dutys)
   {
      this.dutys = dutys;
   }

   public boolean addDuty(Duty duty)
   {
      boolean changed = dutys.add(duty);
      if (changed && duty != null)
      {
         duty.setAssignment(this);
      }
      return changed;
   }

   public boolean removeDuty(Duty duty)
   {
      boolean changed = dutys.remove(duty);
      if (changed && duty != null)
      {
         duty.setAssignment(null);
      }
      return changed;
   }

   public List<Duty> getOpenDutys()
   {
      Set<Duty> tmpDutys = new HashSet<Duty>();
      for (Duty duty : this.getDutys())
      {
         if (!duty.isCatched()) tmpDutys.add(duty);
      }
      List<Duty> dutys = new LinkedList<Duty>(tmpDutys);
      Collections.sort(dutys, utils.ComparatorHelper.dutyComparator);
      return dutys;
   }

   public Set<Duty> getCatchedDutys()
   {
      Set<Duty> dutys = new HashSet<Duty>();
      for (Duty duty : this.getDutys())
      {
         if (duty.isCatched()) dutys.add(duty);
      }
      return dutys;
   }

   public Set<Subtask> getSubtasks()
   {
      return subtasks;
   }

   public void setSubtasks(Set<Subtask> subtasks)
   {
      this.subtasks = subtasks;
   }

   public boolean addSubtask(Subtask subtask)
   {
      boolean changed = subtasks.add(subtask);
      if (changed && subtask != null)
      {
         subtask.setAssignment(this);
      }
      return changed;
   }

   public boolean addMessage(Message message)
   {
      boolean changed = messages.add(message);
      if (changed && message != null)
      {
         message.setAssignment(this);
      }
      return changed;
   }

   public boolean removeSubtask(Subtask subtask)
   {
      boolean changed = subtasks.remove(subtask);
      if (changed && subtask != null)
      {
         subtask.setAssignment(null);
      }
      return changed;
   }

   public boolean removeMessage(Message message)
   {
      boolean changed = messages.remove(message);
      if (changed && message != null)
      {
         message.setAssignment(null);
      }
      return changed;
   }

   public List<Message> getMessages()
   {
      List<Message> sortedMessages = new ArrayList<Message>(messages);
      Collections.sort(sortedMessages);
      return sortedMessages;
   }

   public List<User> getUsersWithAssessment()
   {
      Set<User> users = new LinkedHashSet<User>();
      for (Duty duty : this.getDutys())
      {
         if (duty.isCatched()) users.add(duty.getAssessment().getProofreader());
      }
      List<User> user = new LinkedList<User>(users);
      Collections.sort(user, utils.ComparatorHelper.userComparator);
      return user;
   }
}
