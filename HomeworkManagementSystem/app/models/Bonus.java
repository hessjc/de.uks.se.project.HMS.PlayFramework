package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "bonuses")
@Table(name = "bonuses")
public class Bonus extends HMSModelElement
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @NotNull
   public int percentage;

   @NotNull
   public float bonus;

   @ManyToOne
   public Lecture lecture;

   public static final Finder<Long, Bonus> find = new Finder<Long, Bonus>(
         Long.class, Bonus.class);

   public static List<Bonus> findAll()
   {
      return find.where().findList();
   }

   public static Bonus findGradebonusByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }
   
   public static Bonus createBonus(int percentage, float gradebonus, Lecture lecture) {
      Bonus bonus = new Bonus();
      bonus.setPercentage(percentage);
      bonus.setBonus(gradebonus);
      bonus.save();
      bonus.setLecture(lecture);
      return bonus;
   }

   public long getId()
   {
      return id;
   }

   public int getPercentage()
   {
      return percentage;
   }

   public void setPercentage(int percentage)
   {
      this.percentage = percentage;
   }

   public float getBonus()
   {
      return bonus;
   }

   public void setBonus(float bonus)
   {
      this.bonus = bonus;
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
            oldLecture.removeBonus(this);
         }
         if (lecture != null)
         {
            lecture.addBonus(this);
         }
         this.update();
      }
   }
}
