package controllers

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api._
import models.DakokuSelect
import models.Employee
import models.Dakoku
import anorm.NotAssigned
import java.util.Date

object DakokuController extends Controller {

  val dakokuForm = Form(
      tuple(
      "employee_cd" -> nonEmptyText,
      "dummy" -> text
      ) verifying ("Invalid Employee Cd", result => result match {
        case (employee_cd, _) => Employee.findByCd(employee_cd).isDefined
        })
      )

  def index = Action { implicit request =>
    Ok(views.html.dakoku.index(DakokuSelect.findAll(), dakokuForm))
  }

  def update = Action { implicit request =>
    dakokuForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.dakoku.index(DakokuSelect.findAll(), formWithErrors)),
      {
        case (employee_cd, _) =>
          val employee = Employee.findByCd(employee_cd)
          employee match {
            case Some(e) => 
              Dakoku.create(Dakoku(id = NotAssigned, employee_id = e.id.get,
                  start_work_at = new Date, finish_work_at = null));
              Redirect(routes.DakokuController.index)
            case None => 
              NotFound("NO_DATA")
          }
      })
  }

}