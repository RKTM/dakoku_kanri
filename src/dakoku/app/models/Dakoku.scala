package models

import java.util.Date

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._
import utils.Geo

sealed case class Dakoku(id: Pk[Long],
  employee_id: Long, start_work_at: Date, finish_work_at: Option[Date], start_work_geo: Option[Geo])

sealed case class DakokuSelect(id: Pk[Long],
  employee_id: Long, employee_cd: String, name: String,
  start_work_at: Date, start_work_at_display: String, finish_work_at: Option[Date], start_work_geo: Option[Geo])

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
      get[Option[Date]]("dakoku.finish_work_at") ~
      get[Option[Double]]("start_work_geo_lat") ~
      get[Option[Double]]("start_work_geo_lon") map {
        case id ~ employee_id ~ employee_cd ~ name ~ start_work_at ~
          finish_work_at ~ start_work_geo_lat ~ start_work_geo_lon =>
          DakokuSelect(
            id, employee_id, employee_cd, name, start_work_at,
            smd.format(start_work_at), finish_work_at,
            (start_work_geo_lat, start_work_geo_lon) match {
              case (Some(lat), Some(lon)) => Option(Geo(lat, lon))
              case _ => None
            })
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
          , dakoku.start_work_geo[0] as start_work_geo_lat
          , dakoku.start_work_geo[1] as start_work_geo_lon
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

    //latとlonを取得
    val (lat, lon) = dakoku.start_work_geo match {
      case Some(geo) => (geo.lat, geo.lon)
      case _ => (null, null)
    }

    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into dakoku(
           employee_id, start_work_at, start_work_geo
          ) values (
          {employee_id}, {start_work_at}, point({start_work_geo_lat}, {start_work_geo_lon})
          )
        """).on(
          'employee_id -> dakoku.employee_id,
          'start_work_at -> new Date,
          'start_work_geo_lat -> lat,
          'start_work_geo_lon -> lon).executeUpdate()

      dakoku
    }
  }

}
