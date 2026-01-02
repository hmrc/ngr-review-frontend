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

class NGRNotifyConnectorSpec extends MockHttpV2 with TestData {
  val ngrConnector: NGRNotifyConnector = new NGRNotifyConnector(mockHttpClientV2, mockConfig)
  val credId: CredId = CredId("1234")
  val assessmentId = AssessmentId("1234")

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
        s"${mockConfig.nextGenerationRatesNotifyUrl}/example/$assessmentId",
        Seq("Content-Type" -> "application/json")
      )(HttpResponse(NOT_FOUND, ""))
      val result: Future[Int] = ngrConnector.postPropertyChanges(userAnswers, assessmentId)
      result.futureValue mustBe NOT_FOUND
    }

    "endpoint returns an error" in {
      mockConfig.features.bridgeEndpointEnabled(true)
      setupMockHttpV2PostWithHeaderCarrier(
        s"${mockConfig.nextGenerationRatesNotifyUrl}/example/$assessmentId",
        Seq("Content-Type" -> "application/json")
      )(HttpResponse(ACCEPTED, ""))

      val result: Future[Int]  = ngrConnector.postPropertyChanges(userAnswers, assessmentId)
      result.futureValue mustBe ACCEPTED

    }
  }
}


