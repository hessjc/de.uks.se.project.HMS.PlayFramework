package utils;

import static play.mvc.Controller.flash;
import static utils.Constants.UPLOAD_BASE_FOLDER;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import models.Assignment;
import models.Duty;
import models.User;

import org.joda.time.DateTime;

import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import be.objectify.deadbolt.java.actions.Dynamic;

public class FileHelper
{
   /**
    * upload a duty
    * 
    * @param user
    * @param idA
    * @param body
    * @param filePart
    */
   public static boolean upload(User user, Assignment assignment, MultipartFormData body, FilePart filePart)
   {
      Duty duty = Duty.findDutyByUserANDAssignment(user, assignment);
      if (assignment != null && filePart != null)
      {
         String fileName = filePart.getFilename();
         String fileEnding = fileName.toLowerCase().substring(fileName.lastIndexOf("."));
         if (fileEnding.equals(".zip") || fileEnding.equals(".pdf") || fileEnding.equals(".jar"))
         {
            if (duty == null)
            {
               duty = Duty.createDuty(getDutyFileAsString(user, assignment, fileEnding),
                     DateTime.now(), user, assignment);
            }
               File dutyFile = filePart.getFile();
               File destFile = getDutyFile(user, assignment, fileEnding);
               copyFile(dutyFile, destFile);
               Duty.editDuty(duty, getDutyFileAsString(user, assignment, fileEnding), DateTime.now());
            return true;
         }
         else
         {
            flash("error", Messages.get("duty.upload.wrongformat"));
            return false;
         }
      }
      return false;
   }
   

   @Dynamic("IsParticipantOfLecture")
   public static void copyFile(File src, File dest)
   {
      FileChannel srcChannel = null;
      FileChannel destChannel = null;
      try
      {
         srcChannel = new FileInputStream(src).getChannel();
         destChannel = new FileOutputStream(dest).getChannel();
         srcChannel.transferTo(0, srcChannel.size(), destChannel);
      }
      catch (IOException e)
      {
         Logger.debug("File for an assignment could not be copied", e);
      }
      finally
      {
         try
         {
            if (srcChannel != null)
            {
               srcChannel.close();
            }
            if (destChannel != null)
            {
               destChannel.close();
            }
         }
         catch (IOException e)
         {
            Logger.debug("After copying a file for an assignment the streams were not closed correctly", e);
         }
      }
   }
   
   public static File getDutyFile(final User user, final Assignment assignment, final String fileEnding)
   {
      String semester = assignment.getLecture().getSemester().getSemester();
      String lectureName = assignment.getLecture().getName();

      String path = (getDutyPath(semester, lectureName, assignment.getName(), user.getMatrikelNumber())
            + assignment.getName()
            + "_" + user.getMatrikelNumber() + fileEnding).replaceAll(" ", "_");
      return new File(Play.application().path() + path);
   }
   
   private static String getDutyFileAsString(final User user, final Assignment assignment, final String fileEnding)
   {
      String semester = assignment.getLecture().getSemester().getSemester();
      String lectureName = assignment.getLecture().getName();

      String path = (getDutyPath(semester, lectureName, assignment.getName(), user.getMatrikelNumber())
            + assignment.getName()
            + "_" + user.getMatrikelNumber() + fileEnding).replaceAll(" ", "_");
      return path;
   }
   
   @Dynamic("IsParticipantOfLecture")
   private static String getDutyPath(String semester, String lectureName, String assignmentName, String userMatrikelNumber)
   {
      String semesterparts[] = semester.split("/");
      if (semesterparts.length > 1)
      {
         semester = semesterparts[0] + "_" + semesterparts[1];
      }
      String path = (UPLOAD_BASE_FOLDER + semester + "/" + lectureName + "/"
            + assignmentName + "/" + userMatrikelNumber).replaceAll(" ", "_");
      File dirs = new File(Play.application().path() + path);
      dirs.mkdirs();
      path = path + "/";
      return path;
   }
}
