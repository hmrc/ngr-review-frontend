/*
 * Copyright 2026 HM Revenue & Customs
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
import models.{AssessmentId, LevelSummary, PhysicalDetails, ReviewDetails, UserAnswers}
import play.api.i18n.{I18nSupport, Lang, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.Content
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryListRow, Value}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.ReviewDetailsHelper
import viewmodels.Section
import viewmodels.govuk.all.{SummaryListRowViewModel, stringToKey}
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*
import views.html.ReviewDetailsView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ReviewDetailsController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  notifyConnector: NGRNotifyConnector,
  val controllerComponents: MessagesControllerComponents,
  view: ReviewDetailsView
)(implicit ec: ExecutionContext,
  appConfig: AppConfig
) extends FrontendBaseController
  with I18nSupport {

  def onPageLoad(assessmentId: AssessmentId): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>
      notifyConnector.getReviewDetails(assessmentId) map {
        case Some(reviewDetails) =>
          val sectionList: Seq[Section] = ReviewDetailsHelper.createSectionList(reviewDetails)
          Ok(view(createDefaultNavBar(), reviewDetails.fullAddress.getOrElse(request.property.addressFull), assessmentId, sectionList))
        case None                =>
          InternalServerError("Could not retrieve review details")
      }
  }
}
