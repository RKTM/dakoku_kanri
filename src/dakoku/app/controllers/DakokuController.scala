package controllers

import java.util.Date

import anorm.NotAssigned
import models.Dakoku
import models.DakokuSelect
import models.Employee
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api._

object DakokuController extends Controller {

  val dakokuForm = Form(
      "employee_cd" -> nonEmptyText
      )

  def index = Action { implicit request =>
    Ok(views.html.dakoku.index(DakokuSelect.findAll(), dakokuForm))
  }

  def update() = Action { implicit request =>
    dakokuForm.bindFromRequest.fold(
      errors => BadRequest,
      {
        case (employee_cd) =>
          //従業員コードで検索
          val employee = Employee.findByCd(employee_cd)
          employee match {
            case Some(e) => 
              val dakoku = Dakoku.create(Dakoku(id = NotAssigned, employee_id = e.id.get,
                  start_work_at = new Date, finish_work_at = null));
              Redirect(routes.EmployeeController.index)
            case None => NotFound
          }
      })
  }

}