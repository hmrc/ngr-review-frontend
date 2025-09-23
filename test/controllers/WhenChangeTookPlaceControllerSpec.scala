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
import forms.WhenChangeTookPlaceFormProvider
import helpers.ControllerSpecSupport
import models.{NormalMode, UserAnswers, WhenChangeTookPlace}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhenChangeTookPlacePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository
import views.html.WhenChangeTookPlaceView

import scala.concurrent.Future

class WhenChangeTookPlaceControllerSpec extends ControllerSpecSupport {
  lazy val view: WhenChangeTookPlaceView = inject[WhenChangeTookPlaceView]
  lazy val formProvider: WhenChangeTookPlaceFormProvider = WhenChangeTookPlaceFormProvider()
  private val controller: WhenChangeTookPlaceController = new WhenChangeTookPlaceController(
    sessionRepository = mockSessionRepository,
    navigator = navigator,
    identify = fakeAuth,
    getData = fakeData(Some(UserAnswers("id"))),
    formProvider = formProvider,
    controllerComponents = mcc,
    view = view
  )

  "AnythingElseController" should {
    "onPageLoad" must {
      "return 200" in {
        val result = controller.onPageLoad(NormalMode)(authenticatedFakeRequest)
        status(result) mustBe OK
      }
      "return HTML" in {
        val result = controller.onPageLoad(NormalMode)(authenticatedFakeRequest)
        contentType(result) mustBe Some("text/html")
        charset(result) mustBe Some("utf-8")
      }
    }
    "onSubmit" must {
      "redirect with valid form" in {
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
        val formRequest = requestWithForm(Map("value" -> "false"))
        val result = controller.onSubmit(NormalMode)(formRequest)
        status(result) mustBe 303
      }
      "bad request if no date and yes selected" in {
        val formRequest = requestWithForm(Map("value" -> "true"))
        val result = controller.onSubmit(NormalMode)(formRequest)
        status(result) mustBe 400
      }
      "bad request with invalid form" in {
        val result = controller.onSubmit(NormalMode)(authenticatedFakeRequest)
        status(result) mustBe 400
      }
    }
  }

}