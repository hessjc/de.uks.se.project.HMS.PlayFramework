package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.format.Formats.DateTime;

@Entity(name = "messages")
@Table(name = "messages")
public class Message extends HMSModelElement implements Comparable
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public long id;

   @Column(length = 65535, columnDefinition = "Text")
   public String body;

   @DateTime(pattern = "dd.MM.yyyy hh:mm:ss")
   public org.joda.time.DateTime date;

   @OneToMany(mappedBy = "parent")
   public Set<Message> answers = new LinkedHashSet<Message>();

   @ManyToOne
   public Message parent;

   @ManyToOne
   public Duty duty;

   @ManyToOne
   public Lecture lecture;

   @ManyToOne
   public Assignment assignment;

   @ManyToOne
   public User sender;

   public static final Finder<Long, Message> find = new Finder<Long, Message>(
         Long.class, Message.class);

   public static Message findMessageByID(long id)
   {
      return find.where().eq("id", id).findUnique();
   }

   public static List<Message> findAllMessagesFromSenderWithId(long id)
   {
      return find.where().eq("sender_id", id).findList();
   }

   public static List<Message> findAllLectureMessagesFromSenderWithId(long id)
   {
      Set<Message> messages = new LinkedHashSet<Message>();
      for (Message message : findAllMessagesFromSenderWithId(id))
      {
         while (message.getParent() != null)
         {
            message = message.getParent();
         }
         if (message.getLecture() != null)
         {
            messages.add(message);
         }
      }

      List<Message> returnMessages = new ArrayList<Message>();

      for (Message message : messages)
      {
         returnMessages.add(message);
      }

      return returnMessages;
   }

   public static List<Message> findAllAssignmentMessagesFromSenderWithId(long id)
   {
      Set<Message> messages = new LinkedHashSet<Message>();
      for (Message message : findAllMessagesFromSenderWithId(id))
      {
         while (message.getParent() != null)
         {
            message = message.getParent();
         }
         if (message.getAssignment() != null)
         {
            messages.add(message);
         }
      }

      List<Message> returnMessages = new ArrayList<Message>();

      for (Message message : messages)
      {
         returnMessages.add(message);
      }

      return returnMessages;
   }

   public static Message createMessage(String text, Lecture lecture, Assignment assignment, Duty duty, User user)
   {
      Message message = new Message();
      message.setBody(text);
      message.setDate(new org.joda.time.DateTime());
      message.save();
      if (assignment != null)
      {
         message.setAssignment(assignment);
      }
      else if (lecture != null)
      {
         message.setLecture(lecture);
      }
      else if (duty != null)
      {
         message.setDuty(duty);
      }
      message.setSender(user);
      return message;
   }

   public static Message createAnswer(String text, long id, User user)
   {
      Message message = new Message();
      message.setBody(text);
      message.setDate(new org.joda.time.DateTime());
      message.save();
      message.setParent(Message.findMessageByID(id));
      message.setSender(user);
      return message;
   }

   public List<Message> getAnswers()
   {
      List<Message> sortedAnswers = new ArrayList<Message>(answers);
      Collections.sort(sortedAnswers);
      return sortedAnswers;
   }

   public void setLecture(Lecture lecture)
   {
      if (this.lecture != lecture)
      {
         Lecture oldLecture = this.lecture;
         this.lecture = lecture;
         if (oldLecture != null)
         {
            oldLecture.removeMessage(this);
         }
         if (lecture != null)
         {
            lecture.addMessage(this);
         }
         this.update();
      }
   }

   public void setAssignment(Assignment assignment)
   {
      if (this.assignment != assignment)
      {
         Assignment oldAssignment = this.assignment;
         this.assignment = assignment;
         if (oldAssignment != null)
         {
            oldAssignment.removeMessage(this);
         }
         if (assignment != null)
         {
            assignment.addMessage(this);
         }
         this.update();
      }
   }
   
   public void setDuty(Duty duty)
   {
      if (this.duty != duty)
      {
         Duty oldDuty = this.duty;
         this.duty = duty;
         if (oldDuty != null)
         {
            oldDuty.removeMessage(this);
         }
         if (duty != null)
         {
            duty.addMessage(this);
         }
         this.update();
      }
   }

   public void setParent(Message parent)
   {
      if (this.parent != parent)
      {
         Message oldParent = this.parent;
         this.parent = parent;
         if (oldParent != null)
         {
            oldParent.removeAnswer(this);
         }
         if (parent != null)
         {
            parent.addAnswer(this);
         }
         this.update();
      }
   }

   public void setSender(User sender)
   {
      if (this.sender != sender)
      {
         User oldSender = this.sender;
         this.sender = sender;
         if (oldSender != null)
         {
            oldSender.removeMessage(this);
         }
         if (sender != null)
         {
            sender.addMessage(this);
         }
         this.update();
      }
   }

   public boolean removeAnswer(Message message)
   {
      boolean changed = answers.remove(message);
      if (changed && message != null)
      {
         message.setAssignment(null);
      }
      return changed;
   }

   public boolean addAnswer(Message message)
   {
      boolean changed = answers.add(message);
      if (changed && message != null)
      {
         message.setParent(this);
      }
      return changed;
   }

   public long getId()
   {
      return id;
   }

   public String getBody()
   {
      return body;
   }

   public void setBody(String body)
   {
      this.body = body;
   }

   public String getHead()
   {
      String head;
      if (body.length() > 29)
      {
         head = body.substring(0, 29) + "...";
      }
      else
      {
         head = body;
      }
      return dropHTML(head);
   }
   
   
   public String getShortHead()
   {
      String head;
      if (body.length() > 25)
      {
         head = body.substring(0, 25) + "...";
      }
      else
      {
         head = body;
      }
      return dropHTML(head);
   }
   
   public String getVeryShortHead()
   {
      String head;
      if (body.length() > 19)
      {
         head = body.substring(0, 19) + "...";
      }
      else
      {
         head = body;
      }
      return dropHTML(head);
   }
   
   private String dropHTML(final String insert)
   {
      final Pattern pattern = Pattern.compile("<([A-Za-z][A-Za-z0-9]*)\\b[^>]*>(.*?)</\\1>");
      String tmpMessage = dropHTMLTag(pattern, insert);
      return tmpMessage.replaceAll("<br>", "");
   }

   private String dropHTMLTag(final Pattern pattern, final String insert)
   {
      final Matcher matcher = pattern.matcher(insert);
      if (matcher.find())
      {
         return dropHTMLTag(pattern, matcher.replaceFirst(matcher.group(2)));
      }
      else
      {
         return insert;
      }
   }

   public Message getParent()
   {
      return parent;
   }

   public Assignment getAssignment()
   {
      return assignment;
   }

   public org.joda.time.DateTime getDate()
   {
      return date;
   }

   public void setDate(org.joda.time.DateTime date)
   {
      this.date = date;
   }

   public Duty getDuty()
   {
      return duty;
   }

   public Lecture getLecture()
   {
      return lecture;
   }

   public Message getConversationParent()
   {
      Message message = this;
      while (message.getParent() != null)
      {
         message = message.getParent();
      }
      return message;
   }

   public Lecture getConversationLecture()
   {
      Message message = getConversationParent();
      if (message.getLecture() != null)
      {
         return message.getLecture();
      }
      else if(message.getAssignment() != null)
      {
         return message.getAssignment().getLecture();
      } else {
         return message.getDuty().getAssignment().getLecture();
      }
   }

   public Assignment getConversationAssignment()
   {
      Message message = getConversationParent();
      return message.getAssignment();
   }
   
   public Duty getConversationDuty()
   {
      Message message = getConversationParent();
      return message.getDuty();
   }

   public Semester getSemester()
   {
      return getConversationLecture().getSemester();
   }

   public User getSender()
   {
      return sender;
   }

   public User getConversationCreator()
   {
      Message message = this;
      while (message.getParent() != null)
      {
         message = message.getParent();
      }
      return message.getSender();
   }

   @Override
   public int compareTo(Object object)
   {
      org.joda.time.DateTime thisDate = getDate();
      org.joda.time.DateTime objectDate = ((Message)object).getDate();

      if (!getAnswers().isEmpty())
      {
         thisDate = findNewestDate(getAnswers(), thisDate);
      }

      if (!((Message)object).getAnswers().isEmpty())
      {
         objectDate = findNewestDate(((Message)object).getAnswers(), objectDate);
      }

      return (-1) * thisDate.compareTo(objectDate);
   }

   private org.joda.time.DateTime findNewestDate(List<Message> list, org.joda.time.DateTime date)
   {
      org.joda.time.DateTime newestDate = date;
      for (Message message : list)
      {
         if (message.getAnswers().isEmpty())
         {
            if (date.compareTo(message.getDate()) == -1)
            {
               newestDate = message.getDate();
            }
         }
         else
         {
            newestDate = findNewestDate(message.getAnswers(), newestDate);
         }
      }
      return newestDate;
   }

}
