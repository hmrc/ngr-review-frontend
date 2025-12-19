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

package helpers

import actions.{FakeDataRequiredAction, FakeDataRetrievalAction, FakeIdentifierAction, FakeRegistrationAction}
import connectors.NGRNotifyConnector
import models.{AssessmentId, UserAnswers}
import navigation.Navigator
import play.api.test.Helpers.stubMessagesControllerComponents
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

trait ControllerSpecSupport extends TestSupport with TestData {

  val assessmentId = AssessmentId("85141561000L")

  val fakeAuth = new FakeIdentifierAction(stubMessagesControllerComponents().parsers)
  val fakeReg = new FakeRegistrationAction(stubMessagesControllerComponents().parsers)
  def fakeData(answers: Option[UserAnswers]) = new FakeDataRetrievalAction(answers)
  def fakeRequireData(answers: Option[UserAnswers]) = new FakeDataRequiredAction(answers)
  val mockSessionRepository: SessionRepository = mock[SessionRepository]
  val mockNGRNotifyConnector: NGRNotifyConnector = mock[NGRNotifyConnector]
  val navigator: Navigator = inject[Navigator]
  val emptyUserAnswers: UserAnswers = UserAnswers("id")

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
  
}
