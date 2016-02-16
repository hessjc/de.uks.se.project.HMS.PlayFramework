package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "semesters")
@Table(name = "semesters")
public class Semester extends HMSModelElement implements Comparable<Semester>
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @NotNull
   public String semester;

   @OneToMany(mappedBy = "semester")
   public Set<Lecture> lectures = new LinkedHashSet<Lecture>();

   public static final Finder<Long, Semester> find = new Finder<Long, Semester>(
         Long.class, Semester.class);

   public static List<Semester> findAll()
   {
      List<Semester> semesters = find.where().order().desc("id").findList();
      Collections.sort(semesters);
      return semesters;
   }

   public static Semester findSemester(String semester)
   {
      return find.where().eq("semester", semester).findUnique();
   }

   public static List<String> findSemestersAsList()
   {
      List<String> semesters = new ArrayList<String>();
      for (Semester semester : findAll())
      {
         semesters.add(semester.getSemester());
      }
      return semesters;
   }

   public static List<Long> findSemesterIDsAsList()
   {
      List<Long> semesters = new ArrayList<Long>();
      for (Semester semester : findAll())
      {
         semesters.add(semester.getId());
      }
      return semesters;
   }

   public static Semester createSemester(String name)
   {
      Semester semester = new Semester();
      semester.setSemester(name);
      semester.save();
      return semester;
   }

   public static void editSemester(Semester semester, String name)
   {
      semester.setSemester(name);
      semester.update();
   }

   public static void deleteSemester(Semester semester)
   {
      ArrayList<Lecture> lectureList = new ArrayList<Lecture>();
      lectureList.addAll(semester.getLectures());
      
      for(Lecture lecture : lectureList) {
         Lecture.deleteLecture(lecture);
      }
      semester.delete();
   }

   public static Pattern SS_Pattern = Pattern.compile("SS (\\d{4})");
   public static Pattern WS_Pattern = Pattern.compile("WS (\\d{4})/(\\d{4})");

   @Override
   public int compareTo(Semester o)
   {
      return getYear(o.getSemester()).compareTo(getYear(getSemester()));
   }

   private String getYear(String sem)
   {
      Matcher ss_matcher = SS_Pattern.matcher(sem);
      if (ss_matcher.find())
      {
         return ss_matcher.group(1) + "1";
      }

      Matcher ws_matcher = WS_Pattern.matcher(sem);
      if (ws_matcher.find())
      {
         return ws_matcher.group(1) + "2";
      }
      return "";
   }

   public long getId()
   {
      return id;
   }

   public String getSemester()
   {
      return semester;
   }

   public void setSemester(String semester)
   {
      this.semester = semester;
   }

   public Set<Lecture> getLectures()
   {
      return lectures;
   }

   public boolean addLecture(Lecture lecture)
   {
      boolean changed = lectures.add(lecture);
      if (changed && lecture != null)
      {
         lecture.setSemester(this);
      }
      return changed;
   }

   public boolean removeLecture(Lecture lecture)
   {
      boolean changed = lectures.remove(lecture);
      if (changed && lecture != null)
      {
         lecture.setSemester(null);
      }
      return changed;
   }
}
