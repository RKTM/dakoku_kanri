package models

import java.util.{ Date }

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Employee(id: Pk[Long], employee_cd: String, name: String)

object Employee {

  // -- Parsers

  /**
   * Parse a Employee from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("employee.id") ~
      get[String]("employee.employee_cd") ~
      get[String]("employee.name") map {
        case id ~ employee_cd ~ name => Employee(
          id, employee_cd, name)
      }
  }

  // -- Queries

  /**
   * Retrieve a employee from the id.
   */
  def findById(id: Long): Option[Employee] = {
    DB.withConnection { implicit connection =>
      SQL("select * from employee where id = {id}").on(
        'id -> id).as(Employee.simple.singleOpt)
    }
  }

   /**
   * Retrieve employees from the cd.
   */
  def findByCd(cd: String): Option[Employee] = {
    DB.withConnection { implicit connection =>
      SQL("select * from employee where employee_cd = {cd}").on(
        'cd -> cd).as(Employee.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve all employees.
   */
  def findAll(): List[Employee] = {
    DB.withConnection { implicit connection =>
      SQL("select * from employee order by employee_cd").as(Employee.simple *)
    }
  }

  /**
   * Create a Employee.
   */
  def create(employee: Employee): Employee = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into employee(
           employee_cd, name
          ) values (
          {employee_cd}, {name}
          )
        """).on(
          'employee_cd -> employee.employee_cd,
          'name -> employee.name
        ).executeUpdate()

      employee
    }
  }

}
