package controllers

import play.api.mvc._
import play.api._
import play.api.data._
import play.api.data.Forms._
import models.Employee
import anorm.NotAssigned

object EmployeeController extends Controller {

  val employeeForm = Form(
    tuple(
      "employee_cd" -> nonEmptyText,
      "name" -> nonEmptyText))

  def index = Action { implicit request =>
    Ok(views.html.employee.index(Employee.findAll(), employeeForm))
  }

  def createEmployee() = Action { implicit request =>
    employeeForm.bindFromRequest.fold(
      errors => BadRequest,
      {
        case (employee_cd, name) =>
          val employee = Employee.create(
            Employee(NotAssigned, employee_cd, name))
          Redirect(routes.EmployeeController.index)
      })
  }

}