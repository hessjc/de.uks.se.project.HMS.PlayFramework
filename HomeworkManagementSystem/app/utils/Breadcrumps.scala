package utils

import models.Lecture
import controllers.LectureController
import controllers.routes
import models.Assignment
import models.Subtask
import scala.util.matching.Regex

object Breadcrumps {
  val lecturePattern = PlayHelper.context() + """lecture/(\d+).*"""
  val assignmentPattern = lecturePattern + """/assignment/(\d+).*"""
  val subtaskPattern = assignmentPattern + """/subtask/(\d+).*"""

  def findBreadcrumps(request: String): List[(String, String)] =
    {
      try {
        request match {
          case subtaskPattern.r(lecture, assignment, subtask) => findLecture(lecture.toLong) :: findAssignment(lecture.toLong, assignment.toLong) :: findSubtask(lecture.toLong, assignment.toLong, subtask.toLong) :: Nil
          case assignmentPattern.r(lecture, assignment) => findLecture(lecture.toLong) :: findAssignment(lecture.toLong, assignment.toLong) :: Nil
          case lecturePattern.r(lecture) => findLecture(lecture.toLong) :: Nil
          case _ => Nil
        }
      } catch {
        case e: Exception => Nil
      }
    }

  def findLecture(lectureId: Long): (String, String) = {
    Lecture.findLectureByID(lectureId) match {
      case lecture: Lecture => (wrap(s"${lecture.getSemester().getSemester()}: ${lecture.getName()}"), routes.LectureController.showLecture(lectureId).url)
      case _ => throw new IllegalArgumentException
    }
  }

  def findAssignment(lectureId: Long, assignmentId: Long): (String, String) = {
    Assignment.findAssignmentByID(assignmentId) match {
      case assignment: Assignment => (wrap(assignment.getName()), routes.AssignmentController.showAssignment(lectureId, assignmentId).url)
      case _ => throw new IllegalArgumentException
    }
  }

  def findSubtask(lectureId: Long, assignmentId: Long, subtaskId: Long): (String, String) = {
    Subtask.findSubtaskByID(subtaskId) match {
      case subtask: Subtask => (wrap(subtask.name), routes.SubtaskController.editSubtask(lectureId, assignmentId, subtaskId).url)
      case _ => throw new IllegalArgumentException
    }
  }
  
  def wrap(s:String) = {
    if(s.length > 50) {
      s"${s.take(48)}.."
    } else {
      s
    }
  }
}