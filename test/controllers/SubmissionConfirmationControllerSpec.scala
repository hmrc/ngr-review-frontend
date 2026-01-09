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
import helpers.TestData
import models.AssessmentId
import models.NavBarPageContents.createDefaultNavBar
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.DeclarationPage
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.http.NotFoundException
import viewmodels.govuk.SummaryListFluency
import views.html.SubmissionConfirmationView

import scala.concurrent.Future
import scala.util.Try

class SubmissionConfirmationControllerSpec extends SpecBase with SummaryListFluency with TestData {
  "SubmissionConfirmationController" - {
    "must return OK and the correct view for a GET" in {
      val ref         = AssessmentId("1234-1234-1234")
      val userAnswers = emptyUserAnswers.set(DeclarationPage(ref), "1234-1234-1234").success.value

      val application = applicationBuilder(Some(userAnswers)).build()

      implicit val appConfig: FrontendAppConfig = application.injector.instanceOf[FrontendAppConfig]
      running(application) {
        val request = FakeRequest(GET, routes.SubmissionConfirmationController.onPageLoad(ref).url)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[SubmissionConfirmationView]

        status(result)          mustEqual OK
        contentAsString(result) mustEqual view(property.addressFull, "1234-1234-1234", createDefaultNavBar())(request, messages(application)).toString
      }
    }

    "must throw NotFoundException when declaration data is missing" in {
      val ref         = AssessmentId("1234-1234-1234")
      val userAnswers = emptyUserAnswers

      val application = applicationBuilder(Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.SubmissionConfirmationController.onPageLoad(ref).url)
        val result  = route(application, request).value

        whenReady(result.failed) { ex =>
          ex               mustBe a[NotFoundException]
          ex.getMessage mustEqual "Declaration data not found"
        }
      }
    }
  }
}
