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

import connectors.NGRConnector
import helpers.{ControllerSpecSupport, TestData}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.OK
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{await, contentAsString, status}
import uk.gov.hmrc.http.NotFoundException
import views.html.ReviewYourPropertyDetailsView

import scala.concurrent.Future

class ReviewYourPropertyDetailsControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout with TestData {
  val pageTitle = "Review your property details"

  lazy val reviewYourPropertyDetailsView: ReviewYourPropertyDetailsView = inject[ReviewYourPropertyDetailsView]

  def controller() = new ReviewYourPropertyDetailsController(
    view = reviewYourPropertyDetailsView,
    identifierAction = fakeAuth,
    getData = fakeData(None),
    mcc = mcc
  )(mockConfig)

  "ReviewYourPropertyDetailsController" must {
    "method show" must {
      "Return OK and render the 'review your property details' page" in {
        val result = controller().show(assessmentId)(authenticatedFakeRequest)
        status(result)        mustBe OK
        contentAsString(result) must include(pageTitle)
      }
    }
  }
}
