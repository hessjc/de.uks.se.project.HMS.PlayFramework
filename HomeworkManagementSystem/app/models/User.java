package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import play.data.format.Formats.DateTime;
import play.mvc.Http.Session;
import util.HashUtil;
import util.IUser;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

@Entity(name = "users")
@Table(name = "users")
public class User extends HMSModelElement implements Subject, IUser
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @Email
   @Column(unique = true)
   public String email;

   @OneToMany(mappedBy = "sender")
   public Set<Message> messages = new LinkedHashSet<Message>();

   @NotNull
   public String firstName;
   @NotNull
   public String lastName;

   @NotNull
   public String password;

   @Column(unique = true)
   public String matrikelNumber;

   @DateTime(pattern = "HH:mm:ss dd.MM.yyyy")
   public org.joda.time.DateTime lastLogin;

   public boolean emailValidated = false;

   @ManyToOne
   public Roles role;

   @ManyToMany
   @JoinTable(name = "users_lectures")
   public Set<Lecture> lectures = new LinkedHashSet<Lecture>();

   @OneToMany(mappedBy = "user")
   public Set<Duty> dutys = new LinkedHashSet<Duty>();

   @OneToMany(mappedBy = "proofreader")
   public Set<Assessment> assessments = new LinkedHashSet<Assessment>();

   @ManyToMany
   @JoinTable(name = "users_lectureadmins")
   public Set<Lecture> isLectureadminOf = new LinkedHashSet<Lecture>();

   @ManyToMany
   @JoinTable(name = "users_proofreaders")
   public Set<Lecture> isProofreaderOf = new LinkedHashSet<Lecture>();

   public static final Finder<Long, User> find = new Finder<Long, User>(
         Long.class, User.class);

   public static List<User> findAll()
   {
      return find.where().order().asc("lastName").findList();
   }

   public static User findUserByID(long uid)
   {
      return find.where().eq("id", uid).findUnique();
   }

   public static User findUserByEmail(String email)
   {
      return find.where().eq("email", email).findUnique();
   }

   public static User findUserByMatrikelNumber(String matrikelNumber)
   {
      return find.where().eq("matrikel_number", matrikelNumber).findUnique();
   }

   public static User findUser(String user)
   {
      User found = null;
      if (user != null && !user.equals(""))
      {
         // Try to find by id
         try
         {
            found = find.where().eq("id", user).findUnique();
         }
         catch (Exception e)
         {
            // Skip
         }

         // Try to find by email
         if (found == null)
         {
            try
            {
               found = find.where().eq("email", user).findUnique();
            }
            catch (Exception e)
            {
               // Skip
            }
         }

         // Try to find by matriculation number
         if (found == null)
         {
            try
            {
               found = find.where().eq("matrikelNumber", user).findUnique();
            }
            catch (Exception e)
            {
               // Ignore
            }
         }
      }
      return found;
   }

   public static User createUser(String email, String firstName, String lastName, String password,
         String matrikelNumber, Roles role)
   {
      User user = new User();
      user.setEmail(email);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setPassword(HashUtil.computeSha1(password));
      user.save();
      if (matrikelNumber.equals(""))
      {
         user.setRole(role);
      }
      else
      {
         user.setMatrikelNumber(matrikelNumber);
         user.update();
         user.setRole(Roles.findRoleByName("Student"));
      }
      return user;
   }

   public static void deleteUser(User user)
   {
      for (Lecture lecture : user.getLectures())
      {
         lecture.removeUser(user);
      }
      for (Duty duty : user.getDutys())
      {
         Duty.deleteDuty(duty);
      }
      for (Assessment assessment : user.getAssessments())
      {
         Assessment.deleteAssessment(assessment);
      }
      for (Lecture lecture : user.isLectureadminOf())
      {
         lecture.removeLectureadmin(user);
      }
      for (Lecture lecture : user.isProofreaderOf())
      {
         lecture.removeProofreader(user);
      }
      user.getRole().removeUser(user);
      user.delete();
   }

   public static List<Long> findUsersIDsAsList()
   {
      List<Long> users = new ArrayList<Long>();
      for (User user : findAll())
      {
         users.add(user.getId());
      }
      return users;
   }

   public static List<String> findUsersEmailsAsList()
   {
      List<String> users = new ArrayList<String>();
      for (User user : findAll())
      {
         users.add(user.getEmail());
      }
      return users;
   }

   public static List<String> findUsersMatrikelNumbersAsList()
   {
      List<String> users = new ArrayList<String>();
      for (User user : findAll())
      {
         users.add(user.getMatrikelNumber());
      }
      return users;
   }

   public static Set<Assessment> findUsersAssessmentsByLecture(Long idU, Long idL)
   {
      User user = User.findUserByID(idU);
      Lecture lecture = Lecture.findLectureByID(idL);

      Set<Assessment> assessments = new HashSet<Assessment>();
      for (Assessment assessment : user.getAssessments())
      {
         if (assessment.getDuty().getAssignment().getLecture().equals(lecture)) assessments.add(assessment);
      }
      return assessments;
   }

   @Override
   public String getIdentifier()
   {
      return Long.toString(id);
   }

   @Override
   public List<? extends Role> getRoles()
   {
      return null;
   }

   @Override
   public List<? extends Permission> getPermissions()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   public Long getId()
   {
      return id;
   }

   public String getName()
   {
      return getLastName() + ", " + getFirstName();
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public boolean isEmailValidated()
   {
      return emailValidated;
   }

   public void setEmailValidated(boolean validated)
   {
      this.emailValidated = validated;
   }

   public String getFirstName()
   {
      return firstName;
   }

   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }

   public String getLastName()
   {
      return lastName;
   }

   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }

   public String getMatrikelNumber()
   {
      return matrikelNumber;
   }

   public void setMatrikelNumber(String matrikelNumber)
   {
      this.matrikelNumber = matrikelNumber;
   }

   public org.joda.time.DateTime getLastLogin()
   {
      return lastLogin;
   }

   public void setLastLogin(org.joda.time.DateTime lastLogin)
   {
      this.lastLogin = lastLogin;
   }

   public Roles getRole()
   {
      return role;
   }

   public void setRole(Roles role)
   {
      if (this.role != role)
      {
         Roles oldRole = this.role;
         this.role = role;
         if (oldRole != null)
         {
            oldRole.removeUser(this);
         }
         if (role != null)
         {
            role.addUser(this);
         }
         this.update();
      }
   }

   public Set<Lecture> getLectures()
   {
      return lectures;
   }

   public void setLectures(Set<Lecture> lectures)
   {
      this.lectures = lectures;
   }

   public boolean addLecture(Lecture lecture)
   {
      boolean changed = lectures.add(lecture);
      if (changed && lecture != null)
      {
         lecture.addUser(this);
      }
      return changed;
   }

   public boolean removeLecture(Lecture lecture)
   {
      boolean changed = lectures.remove(lecture);
      if (changed && lecture != null)
      {
         lecture.removeUser(this);
      }
      return changed;
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
         duty.setUser(this);
      }
      return changed;
   }

   public boolean removeDuty(Duty duty)
   {
      boolean changed = dutys.remove(duty);
      if (changed && duty != null)
      {
         duty.setUser(null);
      }
      return changed;
   }

   public Set<Assessment> getAssessments()
   {
      return assessments;
   }

   public void setAssessments(Set<Assessment> assessments)
   {
      this.assessments = assessments;
   }

   public boolean addAssessment(Assessment assessment)
   {
      boolean changed = assessments.add(assessment);
      if (changed && assessment != null)
      {
         assessment.setProofreader(this);
      }
      return changed;
   }

   public boolean removeAssessment(Assessment assessment)
   {
      boolean changed = assessments.remove(assessment);
      if (changed && assessment != null)
      {
         assessment.setProofreader(null);
      }
      return changed;
   }

   public Set<Lecture> isLectureadminOf()
   {
      return isLectureadminOf;
   }

   public void setAsLectureadminOf(Set<Lecture> isLectureadminOf)
   {
      this.isLectureadminOf = isLectureadminOf;
   }

   public boolean addAsLectureadmin(Lecture lecture)
   {
      boolean changed = isLectureadminOf.add(lecture);
      if (changed && lecture != null)
      {
         lecture.addLectureadmin(this);
         this.update();
      }
      return changed;
   }

   public boolean removeAsLectureadmin(Lecture lecture)
   {
      boolean changed = isLectureadminOf.remove(lecture);
      if (changed && lecture != null)
      {
         lecture.removeLectureadmin(this);
         this.update();
      }
      return changed;
   }

   public Set<Lecture> isProofreaderOf()
   {
      return isProofreaderOf;
   }

   public void setAsProofreaderOf(Set<Lecture> isProofreaderOf)
   {
      this.isProofreaderOf = isProofreaderOf;
   }

   public boolean addAsProofreader(Lecture lecture)
   {
      boolean changed = isProofreaderOf.add(lecture);
      if (changed && lecture != null)
      {
         lecture.addProofreader(this);
         this.update();
      }
      return changed;
   }

   public boolean addMessage(Message message)
   {
      boolean changed = messages.add(message);
      if (changed && message != null)
      {
         message.setSender(this);
      }
      return changed;
   }

   public boolean removeMessage(Message message)
   {
      boolean changed = messages.remove(message);
      if (changed && message != null)
      {
         message.setSender(null);
      }
      return changed;
   }

   public boolean removeAsProofreader(Lecture lecture)
   {
      boolean changed = isProofreaderOf.remove(lecture);
      if (changed && lecture != null)
      {
         lecture.removeProofreader(this);
         this.update();
      }
      return changed;
   }

   public Set<Message> getMessages()
   {
      return messages;
   }

   public List<Message> getMyMessages()
   {
      Set<Message> unsortedMessages = new LinkedHashSet<Message>();
      List<Message> returnMessages = new ArrayList<Message>();
      for (Assessment assessment : getAssessments())
      {
         for (Message message : assessment.getDuty().getMessages())
         {
            if (message.getAnswers().isEmpty())
            {
               unsortedMessages.add(message);
            }
            addMyAnswer(unsortedMessages, message);
         }
      }
      for (Duty duty : getDutys())
      {
         for (Message message : duty.getMessages())
         {
            if (message.getAnswers().isEmpty())
            {
               unsortedMessages.add(message);
            }
            addMyAnswer(unsortedMessages, message);
         }
      }
      for (Message message : getMessages())
      {
         if (message.getAnswers().isEmpty())
         {
            unsortedMessages.add(message);
         }
         addMyAnswer(unsortedMessages, message);
      }
      for (Lecture lecture : isLectureadminOf())
      {
         for (Message message : lecture.getMessages())
         {
            if (message.getAnswers().isEmpty())
            {
               unsortedMessages.add(message);
            }
            addMyAnswer(unsortedMessages, message);
         }
         for (Assignment assignment : lecture.getAssignments())
         {
            for (Message message : assignment.getMessages())
            {
               if (message.getAnswers().isEmpty())
               {
                  unsortedMessages.add(message);
               }
               addMyAnswer(unsortedMessages, message);
            }
         }
      }
      for (Lecture lecture : isProofreaderOf())
      {
         for (Message message : lecture.getMessages())
         {
            if (message.getAnswers().isEmpty())
            {
               unsortedMessages.add(message);
            }
            addMyAnswer(unsortedMessages, message);
         }
         for (Assignment assignment : lecture.getAssignments())
         {
            for (Message message : assignment.getMessages())
            {
               if (message.getAnswers().isEmpty())
               {
                  unsortedMessages.add(message);
               }
               addMyAnswer(unsortedMessages, message);
            }
         }
      }
      for (Message message : unsortedMessages)
      {
         returnMessages.add(message);
      }
      Collections.sort(returnMessages);
      return returnMessages;
   }

   private void addMyAnswer(Set<Message> list, Message message)
   {
      for (Message answer : message.getAnswers())
      {
         if (answer.getAnswers().isEmpty())
         {
            list.add(answer);
         }
         addMyAnswer(list, answer);
      }
   }

   public boolean canAnswer(Message messageToAnswer)
   {
      Set<Message> messagesToAnswer = new LinkedHashSet<Message>();
      for (Assessment assessment : getAssessments())
      {
         for (Message message : assessment.getDuty().getMessages())
         {
            messagesToAnswer.add(message);
            addAllAnswer(messagesToAnswer, message);
         }
      }
      for (Duty duty : getDutys())
      {
         for (Message message : duty.getMessages())
         {
            messagesToAnswer.add(message);
            addAllAnswer(messagesToAnswer, message);
         }
      }
      for (Message message : getMessages())
      {
         messagesToAnswer.add(message);
         addAllAnswer(messagesToAnswer, message);
      }
      for (Lecture lecture : isLectureadminOf())
      {
         for (Message message : lecture.getMessages())
         {
            messagesToAnswer.add(message);
            addAllAnswer(messagesToAnswer, message);
         }
         for (Assignment assignment : lecture.getAssignments())
         {
            for (Message message : assignment.getMessages())
            {
               messagesToAnswer.add(message);
               addAllAnswer(messagesToAnswer, message);
            }
         }
      }
      return messagesToAnswer.contains(messageToAnswer);
   }

   public boolean canSee(Message messageToSee)
   {
      Set<Message> messagesToSee = new LinkedHashSet<Message>();
      for (Assessment assessment : getAssessments())
      {
         for (Message message : assessment.getDuty().getMessages())
         {
            messagesToSee.add(message);
            addAllAnswer(messagesToSee, message);
         }
      }
      for (Duty duty : getDutys())
      {
         for (Message message : duty.getMessages())
         {
            messagesToSee.add(message);
            addAllAnswer(messagesToSee, message);
         }
      }
      for (Message message : getMessages())
      {
         messagesToSee.add(message);
         addAllAnswer(messagesToSee, message);
      }
      for (Lecture lecture : isLectureadminOf())
      {
         for (Message message : lecture.getMessages())
         {
            messagesToSee.add(message);
            addAllAnswer(messagesToSee, message);
         }
         for (Assignment assignment : lecture.getAssignments())
         {
            for (Message message : assignment.getMessages())
            {
               messagesToSee.add(message);
               addAllAnswer(messagesToSee, message);
            }
         }
      }
      for (Lecture lecture : isProofreaderOf())
      {
         for (Message message : lecture.getMessages())
         {
            messagesToSee.add(message);
            addAllAnswer(messagesToSee, message);
         }
         for (Assignment assignment : lecture.getAssignments())
         {
            for (Message message : assignment.getMessages())
            {
               messagesToSee.add(message);
               addAllAnswer(messagesToSee, message);
            }
         }
      }
      for (Lecture lecture : getLectures())
      {
         for (Message message : lecture.getMessages())
         {
            if (message.getSender().isLectureadminOf().contains(lecture))
            {
               messagesToSee.add(message);
               addAllAnswer(messagesToSee, message);
            }
         }
         for (Assignment assignment : lecture.getAssignments())
         {
            for (Message message : assignment.getMessages())
            {
               if (message.getSender().isLectureadminOf().contains(lecture))
               {
                  messagesToSee.add(message);
                  addAllAnswer(messagesToSee, message);
               }
            }
         }
      }
      return messagesToSee.contains(messageToSee);
   }

   private void addAllAnswer(Set<Message> list, Message message)
   {
      for (Message answer : message.getAnswers())
      {
         list.add(answer);
         addAllAnswer(list, answer);
      }
   }

   public static User findBySession(final Session session)
   {
      AuthUser authUser = PlayAuthenticate.getUser(session);
      if (authUser != null)
      {
         return findUser(authUser.getId());
      }
      return null;
   }

   public boolean hasMessageInLecture(Lecture lecture)
   {
      boolean result = false;
      for (Message message : getMessages())
      {
         if (message.getLecture() != null && message.getLecture().getId() == lecture.getId())
         {
            result = true;
         }
      }
      if (!lecture.getMessages().isEmpty() && (isLectureadminOf().contains(lecture)
            || isProofreaderOf().contains(lecture)))
      {
         result = true;
      }
      return result;
   }

   public boolean hasMessageInAssignment(Assignment assignment)
   {
      boolean result = false;
      for (Message message : getMessages())
      {
         if ((message.getAssignment() != null && message.getAssignment().getId() == assignment.getId()))
         {
            result = true;
         }
      }
      if (!assignment.getMessages().isEmpty() && (isLectureadminOf().contains(assignment.getLecture())
            || isProofreaderOf().contains(assignment.getLecture())))
      {
         result = true;
      }
      return result;
   }
}