import static play.libs.F.Promise.pure;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import models.Assessment;
import models.Assignment;
import models.Duty;
import models.Lecture;
import models.Message;
import models.Roles;
import models.Semester;
import models.Subtask;
import models.User;
import models.Valuation;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.data.format.Formatters;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Results;
import play.mvc.SimpleResult;

import com.avaje.ebean.Ebean;

public class Global extends GlobalSettings
{
   @Override
   public Promise<SimpleResult> onHandlerNotFound(RequestHeader arg0)
   {
      return pure((SimpleResult)Results.notFound(views.html.errors.e404.render()));
   }

   @Override
   public void onStart(Application app)
   {
      Formatters.register(DateTime.class, new Formatters.SimpleFormatter<DateTime>() {
         private final DateTimeFormatter FOR_PATTERN = DateTimeFormat.forPattern("yyyy-MM-dd");

         @Override
         public DateTime parse(String input, Locale l) throws ParseException
         {
            DateTime parseDateTime = FOR_PATTERN.parseDateTime(input);
            return parseDateTime;
         }

         @Override
         public String print(DateTime localTime, Locale l)
         {
            return FOR_PATTERN.print(localTime);
         }
      });

      try
      {
         if (Roles.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-role.yml");
            Ebean.save(all.get("roles"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (User.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-user.yml");
            Ebean.save(all.get("users"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (Semester.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-semester.yml");
            Ebean.save(all.get("semesters"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (Lecture.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-lecture.yml");
            Ebean.save(all.get("lectures"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (Assignment.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-assignment.yml");
            Ebean.save(all.get("assignments"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (Subtask.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-subtask.yml");
            Ebean.save(all.get("subtasks"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (Duty.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-duty.yml");
            Ebean.save(all.get("dutys"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (Assessment.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-assessment.yml");
            Ebean.save(all.get("assessments"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (Valuation.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-valuation.yml");
            Ebean.save(all.get("valuations"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      try
      {
         if (Message.find.findRowCount() == 0)
         {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>)load(app, "yaml/initial-message.yml");
            Ebean.save(all.get("messages"));
         }
      }
      catch (Exception e)
      {
         Logger.error("Error during creation of initial data", e);
      }
      Long counter = (long)1;

      for (Duty duty : Duty.findAll())
      {
         duty.setAssessment(Assessment.findAssessmentByID(counter++));
      }
   }

   private Object load(Application app, String resourceName)
   {
      org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml(new JodaPropertyConstructor(app.classloader()));
      return yaml.load(app.resourceAsStream(resourceName));
   }

   class JodaPropertyConstructor extends CustomClassLoaderConstructor
   {
      public JodaPropertyConstructor(ClassLoader classLoader)
      {
         super(classLoader);
         yamlClassConstructors.put(NodeId.scalar, new TimeStampConstruct());
      }

      class TimeStampConstruct extends Constructor.ConstructScalar
      {
         Tag timestampTag = new Tag("tag:yaml.org,2002:timestamp");
         
         @Override
         public Object construct(Node nnode)
         {
            if (nnode.getTag().equals(timestampTag))
            {
               Construct dateConstructor = yamlConstructors.get(Tag.TIMESTAMP);
               Date date = (Date)dateConstructor.construct(nnode);
               return new DateTime(date, DateTimeZone.UTC);
            }
            else
            {
               return super.construct(nnode);
            }
         }

      }
   }
}
