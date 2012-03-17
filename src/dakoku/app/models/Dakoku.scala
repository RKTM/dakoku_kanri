package models

import java.util.{ Date }

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

sealed case class Dakoku(id: Pk[Long], employee_id: Long, start_work_at: Date, finish_work_at: Option[Date])

sealed case class DakokuSelect(id: Pk[Long], employee_id: Long, employee_cd: String, name: String,
  start_work_at: Date, start_work_at_display: String, finish_work_at: Option[Date])

object DakokuSelect {

  val smd = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm")

  // -- Parsers

  /**
   * Parse a Dakoku from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("dakoku.id") ~
      get[Long]("dakoku.employee_id") ~
      get[String]("employee.employee_cd") ~
      get[String]("employee.name") ~
      get[Date]("dakoku.start_work_at") ~
      get[Option[Date]]("dakoku.finish_work_at") map {
        case id ~ employee_id ~ employee_cd ~ name ~ start_work_at ~ finish_work_at =>
          DakokuSelect(
            id, employee_id, employee_cd, name, start_work_at,
            smd.format(start_work_at), finish_work_at)
      }
  }

  // -- Queries

  /**
   * Retrieve all employees.
   */
  def findAll(): List[DakokuSelect] = {
    DB.withConnection { implicit connection =>
      SQL("""
          select
            dakoku.id
          , dakoku.employee_id
          , employee.employee_cd
          , employee.name
          , dakoku.start_work_at
          , dakoku.finish_work_at
          from 
            dakoku
            inner join employee on
              dakoku.employee_id = employee.id
          order by 
            dakoku.start_work_at desc
          """).as(DakokuSelect.simple *)
    }
  }
}

object Dakoku {

  /**
   * Create a Employee.
   */
  def create(dakoku: Dakoku): Dakoku = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into dakoku(
           employee_id, start_work_at 
          ) values (
          {employee_id}, {start_work_at}
          )
        """).on(
          'employee_id -> dakoku.employee_id,
          'start_work_at -> new Date).executeUpdate()

      dakoku
    }
  }

}
