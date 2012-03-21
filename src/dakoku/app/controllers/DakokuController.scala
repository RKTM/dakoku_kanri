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
import utils.Geo
import play.api.libs.json.Json

object DakokuController extends Controller {

  val dakokuForm = Form(
    tuple(
      "employee_cd" -> nonEmptyText,
      "dummy" -> text) verifying ("Invalid Employee Cd", result => result match {
        case (employee_cd, _) => Employee.findByCd(employee_cd).isDefined
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

  def startWorkFromMobile = Action(parse.json) { request =>

    Logger.debug("startWorkFromMobile")

    //      val employee_cd = queryParagetOrElse((("employee_c, -1d")
    //    val lat = queryParam.gOrElseet("la, -999t")
    //    val lon = queryParam.gOrElseet("lo, -999n

    val employee_cd = request.body \ "employee_cd"
    val lat = request.body \ "lat"
    val lon = request.body \ "lon"

    Logger.debug(employee_cd.toString())
    Logger.debug(lat.toString())
    Logger.debug(lon.toString())

    val employee = Employee.findByCd(employee_cd.toString())

    employee match {
      case Some(e) =>
        val createdDakoku = Dakoku.create(Dakoku(id = NotAssigned, employee_id = e.id.get,
          start_work_at = new Date, finish_work_at = null,
          start_work_geo = Option(Geo(lat.toString().toDouble, lon.toString.toDouble))))

        Ok(Json.toJson(
          Map("status" -> "OK",
            "start_work_at" -> createdDakoku.start_work_at.toString())))

      case None =>
        NotFound(Json.toJson(
          Map("status" -> "Not Found",
            "start_work_at" -> "")))
    }

  }

}