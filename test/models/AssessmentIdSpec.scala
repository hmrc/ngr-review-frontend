/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustEqual
import play.api.libs.json.JsString
import play.api.mvc.PathBindable

class AssessmentIdSpec extends AnyFreeSpec {

  "AssessmentId" - {
    "should create an instance with the correct value" in {
      val id           = "test-id-123"
      val assessmentId = AssessmentId(id)
      assessmentId.value mustEqual id
    }

    "toString should return the correct string representation" in {
      val id           = "test-id-456"
      val assessmentId = AssessmentId(id)
      assessmentId.toString mustEqual id
    }

    "JSON serialization and deserialization" - {
      import play.api.libs.json.Json

      "should serialize AssessmentId to JSON" in {
        val assessmentId = AssessmentId("1234567890")
        val json         = Json.toJson(assessmentId)
        json mustEqual JsString("1234567890")
      }

      "should deserialize JSON to AssessmentId" in {
        val json         = JsString("1234567890")
        val assessmentId = json.as[AssessmentId]
        assessmentId mustEqual AssessmentId("1234567890")
      }
    }
  }

  "pathBindable" - {
    "should bind a valid string to AssessmentId" in {
      val pathBindable = implicitly[PathBindable[AssessmentId]]
      val assessmentId = AssessmentId("AB123")

      val bind: Either[String, AssessmentId] = pathBindable.bind("assessmentId", "AB123")
      bind.value mustEqual assessmentId
    }

    "should unbind a AssessmentId to a string" in {
      val key            = "assessmentId"
      val assessmentId   = AssessmentId("12345678889")
      val unbind: String = AssessmentId.pathBindable.unbind(key, assessmentId)
      unbind mustEqual "12345678889"
    }
  }
}
