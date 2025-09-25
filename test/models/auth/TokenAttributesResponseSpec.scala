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

package models.auth

import org.scalatest.freespec.AnyFreeSpec

class TokenAttributesResponseSpec extends AnyFreeSpec {
  
  "TokenAttributesResponse" - {
    "must serialize and deserialize to/from JSON" in {
      val tokenAttributesResponse = TokenAttributesResponse(authenticationProvider = "", name = Some("SomeValue"), email = None, identity = Some(Identity("TestProvider", None, None)),
        enrolments = Set.empty, "Test Enrolment", Some("Activated"))
      val json = TokenAttributesResponse.format.writes(tokenAttributesResponse)
      val deserializedResponse = TokenAttributesResponse.format.reads(json).get

      assert(deserializedResponse == tokenAttributesResponse)
    }
  }
}
