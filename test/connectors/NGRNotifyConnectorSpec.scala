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

package connectors

import helpers.TestData
import config.AppConfig
import mocks.MockHttpV2
import models.propertyLinking.{PropertyLinkingUserAnswers, VMVProperty}
import models.registration.*
import models.registration.ReferenceType.TRN
import models.{AssessmentId, ReviewChangesUserAnswers}
import org.mockito.Mockito.when
import play.api.http.Status.{ACCEPTED, NOT_FOUND}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.{HttpResponse, NotFoundException}

import java.time.LocalDate
import scala.concurrent.Future
import models.registration.RatepayerRegistration
import models.{AssessmentId, ReviewDetails}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldEqual
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}

class NGRNotifyConnectorSpec extends MockHttpV2 {
  val assessmentId                           = AssessmentId("test-assessment-id")
  val ngrNotifyConnector: NGRNotifyConnector = new NGRNotifyConnector(mockHttpClientV2, mockConfig)

  val userAnswers: ReviewChangesUserAnswers = ReviewChangesUserAnswers(
    declarationRef = Some("1234")
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.bridgeEndpointEnabled(true)
  }

  "postPropertyChanges" when {
    "Successfully return a response  when provided correct body" in {
      setupMockHttpV2PostWithHeaderCarrier(
        s"${mockConfig.ngrNotifyUrl}/ngr-notify/example/$assessmentId",
        Seq("Content-Type" -> "application/json")
      )(HttpResponse(NOT_FOUND, ""))
      val result: Future[Int] = ngrNotifyConnector.postPropertyChanges(userAnswers, assessmentId)
      result.futureValue mustBe NOT_FOUND
    }

    "endpoint returns an error" in {
      mockConfig.features.bridgeEndpointEnabled(true)
      setupMockHttpV2PostWithHeaderCarrier(
        s"${mockConfig.ngrNotifyUrl}/ngr-notify/example/$assessmentId",
        Seq("Content-Type" -> "application/json")
      )(HttpResponse(ACCEPTED, ""))

      val result: Future[Int] = ngrNotifyConnector.postPropertyChanges(userAnswers, assessmentId)
      result.futureValue mustBe ACCEPTED

    }
  }

  "getReviewDetails" when {
    "should call the correct URL" in {
      val sampleResponse: ReviewDetails =
        ReviewDetails(floorsInfo = List.empty, otherAdditionInfo = List.empty, parkingInfo = List.empty, totalArea = 100, fullAddress = None)
      setupMockHttpV2Get(s"${mockConfig.ngrNotifyUrl}/ngr-notify/review-properties/$assessmentId")(Some(sampleResponse))

      val result: Future[Option[ReviewDetails]] = ngrNotifyConnector.getReviewDetails(assessmentId)
      result.futureValue.value mustBe sampleResponse
    }

    "should return None when no data is found" in {
      setupMockHttpV2Get(s"${mockConfig.ngrNotifyUrl}/ngr-notify/review-properties/$assessmentId")(None)

      val result = ngrNotifyConnector.getReviewDetails(assessmentId)
      result.futureValue mustBe None
    }
  }

}
