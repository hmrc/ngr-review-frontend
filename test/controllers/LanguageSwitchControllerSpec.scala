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

package controllers

import base.SpecBase
import helpers.TestData
import org.scalatest.freespec.AnyFreeSpec
import play.api.i18n.Lang

class LanguageSwitchControllerSpec extends SpecBase with TestData {

  val controller: LanguageSwitchController = applicationBuilder(None).build().injector.instanceOf[LanguageSwitchController]

  "LanguageSwitchController" - {
    "must have a fallback URL" in {
      controller.fallbackURL mustBe "/ngr-review-frontend"
    }

    "must have a non-empty language map" in {
      controller.languageMap mustBe Map(
        "en" -> Lang("en"),
        "cy" -> Lang("cy")
      )
    }

  }
}
