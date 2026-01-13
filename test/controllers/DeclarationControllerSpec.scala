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
import config.FrontendAppConfig
import helpers.{ControllerSpecSupport, TestData}
import models.NavBarPageContents.createDefaultNavBar
import models.UserAnswers
import models.registration.CredId
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.TryValues
import pages.*
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import views.html.{DeclarationView, ErrorTemplate}

import java.time.LocalDate
import scala.concurrent.Future

class DeclarationControllerSpec extends ControllerSpecSupport with TryValues {

  lazy val view: DeclarationView            = inject[DeclarationView]
  lazy val errorTemplateView: ErrorTemplate = inject[ErrorTemplate]

  def minUserAnswers: UserAnswers =
    emptyUserAnswers
      .set(DeclarationPage(assessmentId), "some-reference").success.value

  def controllerWithUserAnswers(userAnswers: Option[UserAnswers]) = DeclarationController(
    mcc,
    view,
    fakeAuth,
    fakeData(userAnswers),
    mockSessionRepository
  )

  "Declaration Controller" must {

    ".show" should {
      "correctly render page" in {
        val result = controllerWithUserAnswers(Some(minUserAnswers)).show(assessmentId)(authenticatedFakeRequest)
        status(result)      mustBe 200
        contentType(result) mustBe Some("text/html")
        charset(result)     mustBe Some("utf-8")
      }

    }

    ".next" should {
      "redirect when accepted and DeclarationPage data is present with generated Reference" in {
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
        val result = controllerWithUserAnswers(Some(minUserAnswers)).next(assessmentId)(authenticatedFakeRequest)

        status(result)                 mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.SubmissionConfirmationController.onPageLoad(assessmentId).url
      }
    }
  }

}
