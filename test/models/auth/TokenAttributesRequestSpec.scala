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

class TokenAttributesRequestSpec extends AnyFreeSpec {

  "TokenAttributesRequest" - {
    "must serialize and deserialize to/from JSON" in {
      val tokenAttributesRequest = TokenAttributesRequest("SomeValue")
      val json                   = TokenAttributesRequest.format.writes(tokenAttributesRequest)
      val deserializedRequest    = TokenAttributesRequest.format.reads(json).get

      assert(deserializedRequest == tokenAttributesRequest)
    }
  }

}
