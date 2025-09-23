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

package forms

import forms.behaviours.OptionFieldBehaviours
import models.WhenChangeTookPlace
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages

import java.time.LocalDate

class WhenChangeTookPlaceFormProviderSpec extends OptionFieldBehaviours {

  private implicit val messages: Messages = stubMessages()
  val form = new WhenChangeTookPlaceFormProvider()()

  ".WhenChangeTookPlace" - {

    val fieldName = "value"
    val requiredKey = "whenChangeTookPlace.error.required"
    val dateFieldName = "date"
    val dateRequiredKey = "whenChangeTookPlace.error.required.all"

    "bind mandatory fields" in {
      val result = form.bind(
        Map(
          fieldName -> "true",
          s"$dateFieldName.day" -> "31",
          s"$dateFieldName.month" -> "8",
          s"$dateFieldName.year" -> "2025"
        ))
      result.errors mustBe empty
    }

    "error when no text and true" in {
      val result = form.bind(Map(fieldName -> "true"))
      result.errors mustBe List(FormError(dateFieldName, dateRequiredKey))
    }

    "fail the mandatory field validation on missing data" in {
      val result = form.bind(Map(fieldName -> "", dateFieldName -> ""))
      result.errors mustBe List(
        FormError(fieldName, requiredKey),
      )
    }
  }
}