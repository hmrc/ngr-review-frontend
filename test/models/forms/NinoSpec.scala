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

package models.forms

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe
import play.api.libs.json.Json

class NinoSpec extends AnyFreeSpec {

  "Nino JSON format" - {

    "serialize Nino to JSON" in {
      val nino = Nino("AA123456A")
      val json = Json.toJson(nino)
      json.toString() mustBe """{"nino":"AA123456A"}"""
    }

    "deserialize JSON to Nino" in {
      val json = Json.parse("""{"nino":"AA123456A"}""")
      val nino = json.as[Nino]
      nino mustBe Nino("AA123456A")
    }
  }

}
