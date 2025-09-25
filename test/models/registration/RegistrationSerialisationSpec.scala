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
import play.api.libs.json.Json

class RegistrationSerialisationSpec extends PlaySpec {

  "CredId JSON format" should {
    "serialize and deserialize correctly" in {
      val model = CredId("cred-123")
      val json = Json.toJson(model)
      json.as[CredId] mustEqual model
    }
  }

  "Email JSON format" should {
    "serialize and deserialize correctly" in {
      val model = Email("test@email.com")
      val json = Json.toJson(model)
      json.as[Email] mustEqual model
    }
  }

  "Name JSON format" should {
    "serialize and deserialize correctly" in {
      val model = Name("Test Name")
      val json = Json.toJson(model)
      json.as[Name] mustEqual model
    }
  }

  "TradingName JSON format" should {
    "serialize and deserialize correctly" in {
      val model = TradingName("Trading Name")
      val json = Json.toJson(model)
      json.as[TradingName] mustEqual model
    }
  }

  "PhoneNumber JSON format" should {
    "serialize and deserialize correctly" in {
      val model = PhoneNumber("0123456789")
      val json = Json.toJson(model)
      json.as[PhoneNumber] mustEqual model
    }
  }

  "Postcode JSON format" should {
    "serialize and deserialize correctly" in {
      val model = Postcode("AB12 3CD")
      val json = Json.toJson(model)
      json.as[Postcode] mustEqual model
    }
  }

  "Nino JSON format" should {
    "serialize and deserialize correctly" in {
      val model = Nino("AA123456A")
      val json = Json.toJson(model)
      json.as[Nino] mustEqual model
    }
  }

  "TRNReferenceNumber JSON format" should {
    "serialize and deserialize correctly" in {
      val model = TRNReferenceNumber(ReferenceType.TRN, "TRN-123")
      val json = Json.toJson(model)
      json.as[TRNReferenceNumber] mustEqual model
    }
  }

  "Address JSON format" should {
    "serialize and deserialize correctly" in {
      val model = Address(
        line1 = "1 Test St",
        line2 = Some("Suite 2"),
        town = "Testville",
        county = Some("Testshire"),
        postcode = Postcode("TE5 7ST")
      )
      val json = Json.toJson(model)
      json.as[Address] mustEqual model
    }
  }
}

