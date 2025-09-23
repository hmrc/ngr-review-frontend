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

package actions

import base.SpecBase
import connectors.NGRConnector
import controllers.actions.DataRetrievalActionImpl
import helpers.TestData
import models.UserAnswers
import models.registration.CredId
import models.requests.{IdentifierRequest, OptionalDataRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import repositories.SessionRepository
import uk.gov.hmrc.http.NotFoundException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with MockitoSugar with TestData {

  class Harness(sessionRepository: SessionRepository, ngrConnector: NGRConnector) extends DataRetrievalActionImpl(sessionRepository, ngrConnector) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" - {

    "when there is no data in the cache" - {

      "must set userAnswers to 'None' in the request" in {

        val sessionRepository = mock[SessionRepository]
        val ngrConnector = mock[NGRConnector]
        when(sessionRepository.get(any)).thenReturn(Future(None))
        when(ngrConnector.getLinkedProperty(any)(any)).thenReturn(Future(Some(property)))
        val action = new Harness(sessionRepository, ngrConnector)

        val result = action.callTransform(IdentifierRequest(FakeRequest(), "id", "")).futureValue

        result.userAnswers must not be defined
      }
      
      "must throw NotFoundException when no property is found" in {

        val sessionRepository = mock[SessionRepository]
        val ngrConnector = mock[NGRConnector]
        when(sessionRepository.get(any)).thenReturn(Future(None))
        when(ngrConnector.getLinkedProperty(any)(any)).thenReturn(Future(None))
        val action = new Harness(sessionRepository, ngrConnector)

        intercept[NotFoundException] {
          await(action.callTransform(IdentifierRequest(FakeRequest(), "id", "")))
        }
      }
    }

    "when there is data in the cache" - {

      "must build a userAnswers object and add it to the request" in {

        val sessionRepository = mock[SessionRepository]
        val ngrConnector = mock[NGRConnector]
        when(sessionRepository.get(any)).thenReturn(Future(Some(UserAnswers("id"))))
        when(ngrConnector.getLinkedProperty(any)(any)).thenReturn(Future(Some(property)))
        val action = new Harness(sessionRepository, ngrConnector)

        val result = action.callTransform(new IdentifierRequest(FakeRequest(), "id", "")).futureValue

        result.userAnswers mustBe defined
      }
    }
  }
}
