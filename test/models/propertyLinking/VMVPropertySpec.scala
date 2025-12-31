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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

import java.time.LocalDate

class VMVPropertySpec extends PlaySpec {

  val valuation = Valuation(
    assessmentRef = 123456L,
    assessmentStatus = "Current",
    rateableValue = Some(BigDecimal(10000)),
    scatCode = Some("SCAT1"),
    descriptionText = "Office",
    effectiveDate = LocalDate.parse("2024-04-01"),
    currentFromDate = LocalDate.parse("2024-04-01"),
    listYear = "2023",
    primaryDescription = "Primary",
    allowedActions = List("view"),
    listType = "TypeA",
    propertyLinkEarliestStartDate = Some(LocalDate.parse("2024-04-01"))
  )

  "VMVProperty JSON format" should {
    "serialize and deserialize correctly with all fields present" in {
      val model = VMVProperty(
        uarn = 987654321L,
        addressFull = "123 Test Street, Testville",
        localAuthorityCode = "123",
        localAuthorityReference = "LAREF1",
        valuations = List(valuation)
      )
      val json  = Json.toJson(model)
      json.as[VMVProperty] mustEqual model
    }

    "deserialize from JSON" in {
      val json = Json.parse(
        """
        {
          "uarn": 987654321,
          "addressFull": "123 Test Street, Testville",
          "localAuthorityCode": "123",
          "localAuthorityReference": "LAREF1",
          "valuations": [
            {
              "assessmentRef": 123456,
              "assessmentStatus": "Current",
              "rateableValue": 10000,
              "scatCode": "SCAT1",
              "descriptionText": "Office",
              "effectiveDate": "2024-04-01",
              "currentFromDate": "2024-04-01",
              "listYear": "2023",
              "primaryDescription": "Primary",
              "allowedActions": ["view"],
              "listType": "TypeA",
              "propertyLinkEarliestStartDate": "2024-04-01"
            }
          ]
        }
        """
      )
      json.validate[VMVProperty] mustEqual JsSuccess(
        VMVProperty(
          uarn = 987654321L,
          addressFull = "123 Test Street, Testville",
          localAuthorityCode = "123",
          localAuthorityReference = "LAREF1",
          valuations = List(valuation)
        )
      )
    }
  }
}
