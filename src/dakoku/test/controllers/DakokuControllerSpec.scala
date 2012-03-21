package controllers

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import play.api.test.FakeHeaders
import play.api.libs.json.Json
import play.api.test.FakeApplication

class DakokuControllerSpec extends Specification {

  "Dakoku" should {
    "post by json" in {
      running(FakeApplication()) {

        val fq = FakeRequest("GET", "/dakoku/start_work_from_mobile/",
          FakeHeaders(
            Map("ContentType" -> Seq("application/json"))), Json.toJson(Map("employee_cd" -> "xxx")))

        val result = controllers.DakokuController.startWorkFromMobile()(fq)

        status(result) must equalTo(OK)
        contentType(result) must beSome("applicaton/json")
      }

    }
  }

}