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

import controllers.actions.DataRetrievalAction
import helpers.TestData
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}

import scala.concurrent.{ExecutionContext, Future}

class FakeDataRetrievalAction(answers: Option[UserAnswers]) extends DataRetrievalAction with TestData {
  
  override protected def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = {
    Future.successful(
      OptionalDataRequest(request.request, request.credId, answers, property)
    )
  }
}