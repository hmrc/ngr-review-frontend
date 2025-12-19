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

import controllers.actions.AuthRetrievals
import models.auth.AuthenticatedUserRequest
import play.api.mvc.*
import uk.gov.hmrc.auth.core.Nino

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FakeAuthRetrievals @Inject()(bodyParsers: PlayBodyParsers) extends AuthRetrievals {

  override def invokeBlock[A](request: Request[A], block: AuthenticatedUserRequest[A] => Future[Result]): Future[Result] =  {
    val authRequest = AuthenticatedUserRequest(request, None, None, Some("user@email.com"),  Some("1234"), None, nino = Nino(hasNino = true, Some("AA000003D")))
    block(authRequest)
  }
  override def parser: BodyParser[AnyContent] = bodyParsers.defaultBodyParser

  override protected def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
}
