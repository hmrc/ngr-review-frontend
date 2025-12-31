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
  val pageTitle                   = "Review your property details"
  val mockConnector: NGRConnector = mock[NGRConnector]

  lazy val reviewYourPropertyDetailsView: ReviewYourPropertyDetailsView = inject[ReviewYourPropertyDetailsView]

  def controller() = new ReviewYourPropertyDetailsController(
    reviewYourPropertyDetailsView,
    fakeAuth,
    mockConnector,
    mcc
  )(ec, mockConfig)

  "ReviewYourPropertyDetailsController" must {
    "method show" must {
      "Return OK and render the 'review your property details' page" in {
        when(mockConnector.getLinkedProperty(any())).thenReturn(Future.successful(Some(property)))
        val result = controller().show()(authenticatedFakeRequest)
        status(result)        mustBe OK
        contentAsString(result) must include(pageTitle)
      }
      "Throw exception when no property linking is found" in {
        when(mockConnector.getLinkedProperty(any())).thenReturn(Future.successful(None))
        val exception = intercept[NotFoundException] {
          await(controller().show()(authenticatedFakeRequest))
        }
        exception.getMessage mustBe "Unable to find match Linked Properties"
      }
    }
  }
}
