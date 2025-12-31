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

package controllers.actions

import com.google.inject.ImplementedBy
import config.FrontendAppConfig
import connectors.NGRConnector
import models.registration.CredId
import models.requests.IdentifierRequest
import play.api.mvc.*
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationActionImpl @Inject() (
  ngrConnector: NGRConnector,
  authenticate: IdentifierAction,
  appConfig: FrontendAppConfig,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext
) extends RegistrationAction {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] =

    authenticate.invokeBlock(
      request,
      { implicit authRequest: IdentifierRequest[A] =>
        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(authRequest, authRequest.session)

        val credId = CredId(authRequest.credId)

        ngrConnector.getRatepayer(credId).flatMap { maybeRatepayer =>
          val isRegistered = maybeRatepayer
            .flatMap(_.ratepayerRegistration)
            .flatMap(_.isRegistered)
            .getOrElse(false)

          if (isRegistered) {
            block(authRequest.copy())
          } else {
            redirectToRegister()
          }
        }
      }
    )

  private def redirectToRegister(): Future[Result] =
    Future.successful(Redirect(s"${appConfig.registrationHost}/ngr-login-register-frontend/register"))
}

@ImplementedBy(classOf[RegistrationActionImpl])
trait RegistrationAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]
