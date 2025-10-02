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

package models.propertyLinking

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustEqual
import play.api.libs.json.{JsSuccess, Json}

class CurrentRatepayerSpec extends AnyFreeSpec {

  "CurrentRatepayer JSON format" - {
    "serialize and deserialize correctly with becomeRatepayerDate present" in {
      val model = CurrentRatepayer(isBeforeApril = true, becomeRatepayerDate = Some("2025-04-01"))
      val json = Json.toJson(model)
      json.as[CurrentRatepayer] mustEqual model
    }

    "serialize and deserialize correctly with becomeRatepayerDate as None" in {
      val model = CurrentRatepayer(isBeforeApril = false, becomeRatepayerDate = None)
      val json = Json.toJson(model)
      json.as[CurrentRatepayer] mustEqual model
    }

    "deserialize from JSON with all fields" in {
      val json = Json.parse("""
        |{
        |  "isBeforeApril": true,
        |  "becomeRatepayerDate": "2025-04-01"
        |}
      """.stripMargin)
      json.validate[CurrentRatepayer] mustEqual JsSuccess(CurrentRatepayer(true, Some("2025-04-01")))
    }

    "deserialize from JSON with becomeRatepayerDate missing (None)" in {
      val json = Json.parse("""
        |{
        |  "isBeforeApril": false
        |}
      """.stripMargin)
      json.validate[CurrentRatepayer] mustEqual JsSuccess(CurrentRatepayer(false, None))
    }
  }
}

