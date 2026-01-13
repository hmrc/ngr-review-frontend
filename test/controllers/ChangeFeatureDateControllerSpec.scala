/*
 * Copyright 2026 HM Revenue & Customs
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
import config.AppConfig
import forms.ChangeFeatureDateFormProvider
import helpers.TestData
import models.NavBarPageContents.createDefaultNavBar
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.ChangeFeatureDatePage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository
import views.html.ChangeFeatureDateView

import scala.concurrent.Future

class ChangeFeatureDateControllerSpec extends SpecBase with MockitoSugar with TestData {

  val formProvider = new ChangeFeatureDateFormProvider()
  val form: Form[Boolean] = formProvider()

  lazy val changeFeatureDateRoute: String = routes.ChangeFeatureDateController.onPageLoad(assessmentId).url

  "ChangeFeatureDate Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      implicit val appConfig: AppConfig = application.injector.instanceOf[AppConfig]
      running(application) {
        val request = FakeRequest(GET, changeFeatureDateRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ChangeFeatureDateView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, assessmentId, property.addressFull, createDefaultNavBar())(request, messages(application), appConfig).toString
      }
    }

    "must redirect to the next page when valid selected is 'true'" in {
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .build()
      val appConfig: AppConfig = application.injector.instanceOf[AppConfig]

      running(application) {
        val request =
          FakeRequest(POST, changeFeatureDateRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual s"${appConfig.ngrPhysicalStartUrl}/when-complete-change/${assessmentId.value}"
      }
    }

    "must redirect to the next page when valid selected is 'false'" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .build()
      implicit val appConfig: AppConfig = application.injector.instanceOf[AppConfig]

      running(application) {
        val request =
          FakeRequest(POST, changeFeatureDateRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual s"${appConfig.ngrPhysicalStartUrl}/have-you-changed-use-of-space/${assessmentId.value}"
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      implicit val appConfig: AppConfig = application.injector.instanceOf[AppConfig]
      running(application) {
        val request =
          FakeRequest(POST, changeFeatureDateRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ChangeFeatureDateView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, assessmentId, property.addressFull, createDefaultNavBar())(request, messages(application), appConfig).toString
      }
    }

  }
}
