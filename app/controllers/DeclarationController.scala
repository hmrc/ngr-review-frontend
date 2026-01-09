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

import config.AppConfig
import connectors.NGRNotifyConnector
import controllers.actions.*
import models.NavBarPageContents.createDefaultNavBar
import models.registration.CredId
import models.{AssessmentId, ReviewChangesUserAnswers, UserAnswers}
import pages.*
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.UniqueIdGenerator
import views.html.{DeclarationView, ErrorTemplate}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  view: DeclarationView,
  authenticate: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  connector: NGRNotifyConnector,
  errorTemplate: ErrorTemplate
)(implicit ec: ExecutionContext,
  appConfig: AppConfig
) extends FrontendBaseController
  with I18nSupport
  with Logging {

  def show(assessmentId: AssessmentId): Action[AnyContent] =
    (authenticate andThen getData) {
      implicit request =>
        Ok(view(assessmentId, request.property.addressFull, createDefaultNavBar()))
    }

  def next(assessmentId: AssessmentId): Action[AnyContent] =
    (authenticate andThen getData).async {
      implicit request =>
        val generateRef = UniqueIdGenerator.generateId
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.getOrElse(UserAnswers(request.userId)).set(DeclarationPage(assessmentId), generateRef))
          _              <- sessionRepository.set(updatedAnswers)
          response       <- connector.postPropertyChanges(ReviewChangesUserAnswers(declarationRef = Some(generateRef)), assessmentId)
        } yield response match {
          case ACCEPTED => Redirect(routes.SubmissionConfirmationController.onPageLoad(assessmentId))
        }

    }
}
