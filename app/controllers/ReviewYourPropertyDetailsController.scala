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
import connectors.NGRConnector
import controllers.actions.IdentifierAction
import models.NavBarPageContents.createDefaultNavBar
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ReviewYourPropertyDetailsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewYourPropertyDetailsController @Inject()(view: ReviewYourPropertyDetailsView,
                                                    identifierAction: IdentifierAction,
                                                    ngrConnector: NGRConnector,
                                                    mcc: MessagesControllerComponents)(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] =
    identifierAction.async(implicit request =>
      ngrConnector.getLinkedProperty.flatMap {
        case Some(property) => Future.successful(Ok(view(createDefaultNavBar(), property.addressFull)))
        case None => Future.failed(throw new NotFoundException("Unable to find match Linked Properties"))
      }
    )
}
