package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import play.data.format.Formats.DateTime;

@Entity(name = "dutys")
@Table(name = "dutys")
public class Duty extends HMSModelElement implements Comparable<Duty>
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @NotNull
   public String uploadedFile;

   @NotNull
   @DateTime(pattern = "dd.MM.yyyy hh:mm:ss")
   public org.joda.time.DateTime uploadedDateTime;

   public boolean catched = false;
   public boolean corrected = false;

   @ManyToOne
   public Assignment assignment;

   @OneToOne
   public Assessment assessment;

   @ManyToOne
   public User user;
   
   @OneToMany(mappedBy = "duty")
   public Set<Message> messages = new LinkedHashSet<Message>();

   public static final Finder<Long, Duty> find = new Finder<Long, Duty>(
         Long.class, Duty.class);

   public static List<Duty> findAll()
   {
      return find.all();
   }

   public static Duty findDutyByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }

   public static Duty createDuty(String uploadedFile, org.joda.time.DateTime uploadedDateTime, User user,
         Assignment assignment)
   {
      Duty duty = new Duty();
      duty.setUploadedFile(uploadedFile);
      duty.setUploadedDateTime(uploadedDateTime);
      duty.save();

      duty.setUser(user);
      duty.setAssignment(assignment);

      duty.setAssessment(Assessment.createAssessment(duty));

      return duty;
   }

   public static Duty editDuty(Duty duty, String uploadedFile, org.joda.time.DateTime uploadedDateTime)
   {
      duty.setUploadedFile(uploadedFile);
      duty.setUploadedDateTime(uploadedDateTime);
      duty.update();
      return duty;
   }

   public static void deleteDuty(Duty duty)
   {
      duty.setAssignment(null);
      duty.setUser(null);
      Assessment.deleteAssessment(duty.getAssessment());
   }

   public static void catchDuty(Duty duty, User user)
   {
      duty.setCatched(true);
      duty.update();
      duty.getAssessment().setProofreader(user);
   }

   public static void closeDuty(Duty duty)
   {
      duty.setCorrected(true);
      duty.update();
   }

   public static void releaseDuty(Duty duty)
   {
      duty.getAssessment().getProofreader().getAssessments().remove(duty);

      duty.setCatched(false);
      duty.setCorrected(false);

      duty.update();
      duty.getAssessment().setProofreader(null);
   }

   public static List<Long> findDutyIDsAsList()
   {
      List<Long> dutys = new ArrayList<Long>();
      for (Duty duty : findAll())
      {
         dutys.add(duty.getId());
      }
      return dutys;
   }

   public static Duty findDutyByAssessmentID(long id)
   {
      return find.where().eq("assessment_id", id).findUnique();
   }

   public static List<Duty> findDutysByAssignmentID(long id)
   {
      return find.where().eq("assignment_id", id).findList();
   }

   public static List<Duty> findCatchedDutysByAssignmentID(long id)
   {
      return find.where().conjunction().eq("assignment_id", id).eq("catched", true).findList();
   }

   public static Set<Duty> findNotCatchedDutysByAssignmentID(long id)
   {
      return find.where().conjunction().eq("assignment_id", id).eq("catched", false).findSet();
   }

   public static List<Duty> findCorrectedDutysByAssignmentID(long id)
   {
      return find.where().conjunction().eq("assignment_id", id).eq("corrected", true).findList();
   }

   public static List<Duty> findUploadedDutysByUserID(long id)
   {
      return find.where().eq("user_id", id).findList();
   }

   public static Duty findDutyByUserANDAssignment(User user, Assignment assignment)
   {
      return find.where().conjunction().eq("user_id", user.getId()).eq("assignment_id", assignment.getId())
            .findUnique();
   }

   public long getId()
   {
      return id;
   }

   public String getUploadedFile()
   {
      return uploadedFile;
   }

   public void setUploadedFile(String uploadedFile)
   {
      this.uploadedFile = uploadedFile;
   }

   public org.joda.time.DateTime getUploadedDateTime()
   {
      return uploadedDateTime;
   }

   public void setUploadedDateTime(org.joda.time.DateTime uploadedDateTime)
   {
      this.uploadedDateTime = uploadedDateTime;
   }

   public boolean isCatched()
   {
      return catched;
   }

   public void setCatched(boolean catched)
   {
      this.catched = catched;
   }

   public boolean isCorrected()
   {
      return corrected;
   }

   public void setCorrected(boolean corrected)
   {
      this.corrected = corrected;
   }

   public Assessment getAssessment()
   {
      return assessment;
   }

   public void setAssessment(Assessment assessment)
   {
      if (this.assessment != assessment)
      {
         Assessment oldAssessment = this.getAssessment();
         this.assessment = assessment;
         this.update();
         if (assessment == null)
         {
            oldAssessment.setDuty(this);
            oldAssessment.update();
         }
         else
         {
            this.assessment.setDuty(this);
            this.assessment.getDuty().update();
         }
      }
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
            oldAssignment.removeDuty(this);
         }
         if (assignment != null)
         {
            assignment.addDuty(this);
         }
         this.update();
      }
   }

   public User getUser()
   {
      return user;
   }

   public void setUser(User user)
   {
      if (this.user != user)
      {
         User oldUser = this.user;
         this.user = user;
         if (oldUser != null)
         {
            oldUser.removeDuty(this);
         }
         if (user != null)
         {
            user.addDuty(this);
         }
         this.update();
      }
   }
   
   public boolean addMessage(Message message)
   {
      boolean changed = messages.add(message);
      if (changed && message != null)
      {
         message.setDuty(this);
      }
      return changed;
   }
   
   public boolean removeMessage(Message message)
   {
      boolean changed = messages.remove(message);
      if (changed && message != null)
      {
         message.setDuty(null);
      }
      return changed;
   }
   
   public List<Message> getMessages()
   {
      List<Message> sortedMessages = new ArrayList<Message>(messages);
      Collections.sort(sortedMessages);
      return sortedMessages;
   }
   
   @Override
   public int compareTo(Duty o)
   {
      return ((Long)getId()).compareTo((Long) o.getId());
   }
}
