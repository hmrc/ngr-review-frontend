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

package models.registration

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class RatepayerRegistrationSpec extends PlaySpec {
  "RatepayerRegistration JSON format" should {
    "serialize and deserialize correctly with all fields present" in {
      val model = RatepayerRegistration(
        userType = Some(UserType.Individual),
        agentStatus = Some(AgentStatus.Agent),
        name = Some(Name("Test Name")),
        tradingName = Some(TradingName("Trading Name")),
        email = Some(Email("test@email.com")),
        nino = Some("AA123456A"),
        contactNumber = Some(PhoneNumber("0123456789")),
        secondaryNumber = Some(PhoneNumber("0987654321")),
        address = Some(Address("1 Test St", Some("Suite 2"), "Testville", Some("Testshire"), Postcode("TE5 7ST"))),
        trnReferenceNumber = Some(TRNReferenceNumber(ReferenceType.TRN, "TRN-123")),
        isRegistered = Some(true)
      )
      val json = Json.toJson(model)
      json.as[RatepayerRegistration] mustEqual model
    }

    "serialize and deserialize correctly with only required fields (all None except isRegistered)" in {
      val model = RatepayerRegistration()
      val json = Json.toJson(model)
      json.as[RatepayerRegistration] mustEqual model
    }

    "deserialize from JSON with missing optional fields" in {
      val json = Json.parse(
        """
        {
          "isRegistered": false
        }
        """
      )
      json.validate[RatepayerRegistration] mustEqual JsSuccess(RatepayerRegistration(isRegistered = Some(false)))
    }
  }
}

