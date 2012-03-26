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
import java.lang.Double
import utils.Geo
import play.api.libs.json.Json

object DakokuController extends Controller {

  val dakokuForm = Form(
    tuple(
      "employee_cd" -> nonEmptyText,
      "dummy" -> text) verifying ("Invalid Employee Cd", result => result match {
        case (employee_cd, _) => Employee.findByCd(employee_cd).isDefined
      }))

  val dakokuFormFromMobile = Form(
    tuple(
      "employee_cd" -> nonEmptyText,
      "dummy" -> optional(text),
      "lat" -> optional(text),
      "lon" -> optional(text)) verifying ("Invalid Employee Cd", result => result match {
        case (employee_cd, _, _, _) => Employee.findByCd(employee_cd).isDefined
      }))

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
                start_work_at = new Date, finish_work_at = null, start_work_geo = None))
              Redirect(routes.DakokuController.index)
            case None =>
              NotFound("NO_DATA")
          }
      })
  }

  def dummyFromMobile = Action { implicit request =>
    Ok(views.html.dakoku.dummyFromMobile(dakokuFormFromMobile))
  }

  def startWorkFromMobile2 = Action { implicit request =>

    Logger.debug("startWorkFromMobile2")

    val formBound = dakokuFormFromMobile.bindFromRequest

    formBound.fold(
      formWithErrors => BadRequest,
      {
        case (employee_cd, _, lat, lon) =>

          val employee = Employee.findByCd(employee_cd)

          employee match {
            case Some(e) =>
              Logger.debug(e.toString())
              
              Dakoku.create(Dakoku(id = NotAssigned, employee_id = e.id.get,
                start_work_at = new Date, finish_work_at = null,
                start_work_geo =
                  Some(Geo(Double.parseDouble(lat.getOrElse("0")), Double.parseDouble(lon.getOrElse("0"))))))
              Redirect(routes.DakokuController.index)

            case None =>
              Logger.debug("NONE!")
              NotFound("NO_DATA")
          }
      })
  }

}