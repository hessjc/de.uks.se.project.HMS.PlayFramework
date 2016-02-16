package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

@Entity(name = "lectures")
@Table(name = "lectures")
public class Lecture extends HMSModelElement implements Comparable<Lecture>
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @NotNull
   public String name;
   @Column(length = 65535, columnDefinition = "Text")
   @NotNull
   public String description;

   public DateTime closingdate;

   public int optionalDutys;
   public int lowerProcentualBounderyOfDutys;

   public int minimumPercentageForExamination;

   @ManyToOne
   public Semester semester;

   @ManyToMany
   @JoinTable(name = "users_lectures")
   public Set<User> users = new LinkedHashSet<User>();

   @OneToMany(mappedBy = "lecture")
   public Set<Assignment> assignments = new LinkedHashSet<Assignment>();

   @OneToMany(mappedBy = "lecture")
   public Set<Message> messages = new LinkedHashSet<Message>();

   @ManyToMany
   @JoinTable(name = "users_lectureadmins")
   public Set<User> lectureadmins = new LinkedHashSet<User>();

   @ManyToMany
   @JoinTable(name = "users_proofreaders")
   public Set<User> proofreaders = new LinkedHashSet<User>();

   @OneToMany(mappedBy = "lecture")
   public Set<Bonus> bonuses = new LinkedHashSet<Bonus>();

   public static final Finder<Long, Lecture> find = new Finder<Long, Lecture>(
         Long.class, Lecture.class);

   public static List<Lecture> findAll()
   {
      return find.where().order().asc("name").findList();
   }

   public static Lecture findLectureByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }

   public static List<Lecture> findLecturesByClosingdate(Date date)
   {
      return find.where().eq("closingdate", date).findList();
   }

   public static List<Lecture> findLecturesBySemester(Semester semester)
   {
      return find.where().eq("semester_id", semester.getId()).orderBy("name").findList();
   }

   public static Lecture createLecture(String name, DateTime closingdate, String description, Semester semester,
         String lectureadmin)
   {
      Lecture lecture = new Lecture();
      lecture.setName(name);
      lecture.setDescription(description);
      lecture.save();
      lecture.setClosingdate(closingdate);
      lecture.update();
      lecture.setSemester(semester);
      lecture.addLectureadmin(User.findUser(lectureadmin));
      return lecture;
   }

   public static void editLecture(Lecture lecture, String name, String semester, DateTime closingdate,
         String description)
   {
      lecture.setName(name);
      lecture.setSemester(Semester.findSemester(semester));
      lecture.setClosingdate(closingdate);
      lecture.setDescription(description);
      lecture.update();
   }

   public static void deleteLecture(Lecture lecture)
   {
      ArrayList<User> userList = new ArrayList<User>();
      userList.addAll(lecture.getUsers());
      for (User user : userList)
      {
         user.removeLecture(lecture);
      }

      ArrayList<Assignment> assignmentList = new ArrayList<Assignment>();
      assignmentList.addAll(lecture.getAssignments());
      for (Assignment assignment : assignmentList)
      {
         Assignment.deleteAssignment(assignment);
      }

      ArrayList<User> lectureadminList = new ArrayList<User>();
      lectureadminList.addAll(lecture.getLectureadmins());
      for (User user : lectureadminList)
      {
         user.removeAsLectureadmin(lecture);
      }

      ArrayList<User> proofreaderList = new ArrayList<User>();
      proofreaderList.addAll(lecture.getProofreaders());
      for (User user : proofreaderList)
      {
         user.removeAsProofreader(lecture);
      }

      lecture.setSemester(null);
      lecture.delete();
   }

   public static void addLectureadminToLecture(Lecture lecture, User lectureadmin)
   {
      lecture.getLectureadmins().add(lectureadmin);
      lecture.update();
   }

   public static void addProofreaderToLecture(Lecture lecture, User proofreader)
   {
      lecture.getProofreaders().add(proofreader);
      lecture.update();
   }

   public static void removeLectureadminOfLecture(Lecture lecture, User lectureadmin)
   {
      lecture.getLectureadmins().remove(lectureadmin);
      lecture.update();
   }

   public static void removeProofreaderOfLecture(Lecture lecture, User proofreader)
   {
      lecture.getProofreaders().remove(proofreader);
      lecture.update();
   }

   public static List<User> findAllNonLectureadminsOfLecture(long idL)
   {
      Lecture lecture = findLectureByID(idL);
      List<User> nonLectureadmins = User.findAll();
      for (User lectureadmin : lecture.getLectureadmins())
      {
         nonLectureadmins.remove(lectureadmin);
      }
      return nonLectureadmins;
   }

   public static List<User> findAllNonProofreadersOfLecture(long idL)
   {
      Lecture lecture = findLectureByID(idL);
      List<User> nonProofreaders = User.findAll();
      for (User proofreader : lecture.getProofreaders())
      {
         nonProofreaders.remove(proofreader);
      }
      return nonProofreaders;
   }

   public static List<String> findAllNonLectureadminsOfLectureAsList(long idL)
   {
      Lecture lecture = findLectureByID(idL);
      List<User> nonLectureadmins = User.findAll();
      for (User lectureadmin : lecture.getLectureadmins())
      {
         nonLectureadmins.remove(lectureadmin);
      }
      List<String> nonLectureadminsAsList = new ArrayList<String>();
      for (User lectureadmin : nonLectureadmins)
      {
         nonLectureadminsAsList.add(lectureadmin.getEmail());
      }
      return nonLectureadminsAsList;
   }

   public static List<String> findAllNonProofreadersOfLectureAsList(long idL)
   {
      Lecture lecture = findLectureByID(idL);
      List<User> nonLectureadmins = User.findAll();
      for (User proofreader : lecture.getProofreaders())
      {
         nonLectureadmins.remove(proofreader);
      }
      List<String> nonLectureadminsAsList = new ArrayList<String>();
      for (User proofreader : nonLectureadmins)
      {
         nonLectureadminsAsList.add(proofreader.getEmail());
      }
      return nonLectureadminsAsList;
   }

   public static List<String> findAllUsersWithMatrikelnumberAsList(long idL)
   {
      Lecture lecture = findLectureByID(idL);
      List<String> emails = new ArrayList<String>();
      for (User user : lecture.getUsers())
      {
         emails.add(String.format("%s (%s)", user.getMatrikelNumber(), user.getEmail()));
      }
      Collections.sort(emails);
      return emails;
   }

   public static List<Long> findLectureIDsAsList()
   {
      List<Long> lectures = new ArrayList<Long>();
      for (Lecture lecture : Lecture.findAll())
      {
         lectures.add(lecture.getId());
      }
      return lectures;
   }

   public static Set<Duty> findOpenDutysByLectureID(long id)
   {
      Set<Duty> dutys = new HashSet<Duty>();
      for (Assignment assignment : Lecture.findLectureByID(id).getAssignments())
      {
         for (Duty duty : assignment.getDutys())
         {
            if (!duty.isCatched()) dutys.add(duty);
         }
      }
      return dutys;
   }

   public static List<User> findResponsibleUserWithAssessment(long idL)
   {
      Lecture lecture = Lecture.findLectureByID(idL);
      Set<User> proofreaders = new HashSet<User>();
      Set<User> tmpSet = lecture.getLectureadmins();
      tmpSet.addAll(lecture.getProofreaders());
      for (User proofreader : tmpSet)
      {
         for (Assessment assessment : proofreader.getAssessments())
         {
            if (assessment.getDuty().getAssignment().getLecture().equals(lecture)) proofreaders.add(proofreader);
         }
      }
      List<User> users = new LinkedList<User>(proofreaders);
      Collections.sort(users, utils.ComparatorHelper.userComparator);
      return users;
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

   public DateTime getClosingdate()
   {
      return closingdate;
   }

   public void setClosingdate(DateTime closingdate)
   {
      this.closingdate = closingdate;
   }

   public Semester getSemester()
   {
      return semester;
   }

   public void setSemester(Semester semester)
   {
      if (this.semester != semester)
      {
         Semester oldSemester = this.semester;
         this.semester = semester;
         if (oldSemester != null)
         {
            oldSemester.removeLecture(this);
         }
         if (semester != null)
         {
            semester.addLecture(this);
         }
         this.update();
      }
   }

   public Set<User> getUsers()
   {
      return users;
   }

   public void setUsers(Set<User> users)
   {
      this.users = users;
   }

   public boolean addUser(User user)
   {
      boolean changed = users.add(user);
      if (changed && user != null)
      {
         user.addLecture(this);
         this.update();
      }

      return changed;
   }

   public boolean removeUser(User user)
   {
      boolean changed = users.remove(user);
      if (changed && user != null)
      {
         user.removeLecture(this);
         this.update();
      }

      return changed;
   }

   public Set<Assignment> getAssignments()
   {
      return assignments;
   }

   public void setAssignments(Set<Assignment> assignments)
   {
      this.assignments = assignments;
   }

   public boolean addAssignment(Assignment assignment)
   {
      boolean changed = assignments.add(assignment);
      if (changed && assignment != null)
      {
         assignment.setLecture(this);
      }
      return changed;
   }

   public boolean removeAssignment(Assignment assignment)
   {
      boolean changed = assignments.remove(assignment);
      if (changed && assignment != null)
      {
         assignment.setLecture(null);
      }
      return changed;
   }

   public Set<User> getLectureadmins()
   {
      return lectureadmins;
   }

   public void setLectureadmins(Set<User> lectureadmins)
   {
      this.lectureadmins = lectureadmins;
   }

   public boolean addLectureadmin(User user)
   {
      boolean changed = lectureadmins.add(user);
      if (changed && user != null)
      {
         user.addAsLectureadmin(this);
         this.update();
      }
      return changed;
   }

   public boolean removeLectureadmin(User user)
   {
      boolean changed = lectureadmins.remove(user);
      if (changed && user != null)
      {
         user.removeAsLectureadmin(this);
         this.update();
      }
      return changed;
   }

   public Set<User> getProofreaders()
   {
      return proofreaders;
   }

   public void setProofreaders(Set<User> proofreaders)
   {
      this.proofreaders = proofreaders;
   }

   public boolean addProofreader(User user)
   {
      boolean changed = proofreaders.add(user);
      if (changed && user != null)
      {
         user.addAsProofreader(this);
         this.update();
      }
      return changed;
   }

   public boolean removeProofreader(User user)
   {
      boolean changed = proofreaders.remove(user);
      if (changed && user != null)
      {
         user.removeAsProofreader(this);
         this.update();
      }
      return changed;
   }

   public List<User> getResponsibleUser()
   {
      Set<User> tmpSet = this.getLectureadmins();
      tmpSet.addAll(this.getProofreaders());
      List<User> users = new LinkedList<User>(tmpSet);
      Collections.sort(users, utils.ComparatorHelper.userComparator);
      return users;
   }

   public int getOptionalDutys()
   {
      return optionalDutys;
   }

   public void setOptionalDutys(int optionalDutys)
   {
      this.optionalDutys = optionalDutys;
   }

   public List<Duty> getOpenDutys()
   {
      Set<Duty> tmpDutys = new HashSet<Duty>();
      for (Assignment assignment : this.getAssignments())
      {
         for (Duty duty : assignment.getDutys())
         {
            if (!duty.isCatched()) tmpDutys.add(duty);
         }
      }
      List<Duty> dutys = new LinkedList<Duty>(tmpDutys);
      Collections.sort(dutys, utils.ComparatorHelper.dutyComparator);
      return dutys;
   }

   public List<Duty> getCatchedDutys()
   {
      Set<Duty> tmpDutys = new HashSet<Duty>();
      for (Assignment assignment : this.getAssignments())
      {
         for (Duty duty : assignment.getDutys())
         {
            if (duty.isCatched()) tmpDutys.add(duty);
         }
      }
      List<Duty> dutys = new LinkedList<Duty>(tmpDutys);
      Collections.sort(dutys, utils.ComparatorHelper.dutyComparator);
      return dutys;
   }

   public int getLowerProcentualBounderyOfDutys()
   {
      return lowerProcentualBounderyOfDutys;
   }

   public void setLowerProcentualBounderyOfDutys(int lowerProcentualBounderyOfDutys)
   {
      this.lowerProcentualBounderyOfDutys = lowerProcentualBounderyOfDutys;
   }

   public int getMinimumPercentageForExamination()
   {
      return minimumPercentageForExamination;
   }

   public void setMinimumPercentageForExamination(int minimumPercentageForExamination)
   {
      this.minimumPercentageForExamination = minimumPercentageForExamination;
   }

   public Set<Bonus> getBonuses()
   {
      return bonuses;
   }

   public void setBonuses(Set<Bonus> bonuses)
   {
      this.bonuses = bonuses;
   }

   public boolean addBonus(Bonus bonus)
   {
      boolean changed = bonuses.add(bonus);
      if (changed && bonus != null)
      {
         bonus.setLecture(this);
      }
      return changed;
   }

   public boolean removeBonus(Bonus bonus)
   {
      boolean changed = bonuses.remove(bonus);
      if (changed && bonus != null)
      {
         bonus.setLecture(null);
      }
      return changed;
   }

   @Override
   public int compareTo(Lecture o)
   {
      return name.compareTo(o.name);
   }

   public boolean addMessage(Message message)
   {
      boolean changed = messages.add(message);
      if (changed && message != null)
      {
         message.setLecture(this);
      }
      return changed;
   }

   public boolean removeMessage(Message message)
   {
      boolean changed = messages.remove(message);
      if (changed && message != null)
      {
         message.setLecture(null);
      }
      return changed;
   }

   public Set<Message> getMessages()
   {
      return messages;
   }

   public List<Message> getMessagesAsList()
   {
      List<Message> returnMessages = new ArrayList<Message>();
      for (Message message : getMessages())
      {
         returnMessages.add(message);
      }
      Collections.sort(returnMessages);
      return returnMessages;
   }
}
