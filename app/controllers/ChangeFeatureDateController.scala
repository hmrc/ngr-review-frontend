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
import controllers.actions.*
import forms.ChangeFeatureDateFormProvider
import models.NavBarPageContents.createDefaultNavBar
import models.{AssessmentId, Mode}
import navigation.Navigator
import pages.ChangeFeatureDatePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ChangeFeatureDateView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ChangeFeatureDateController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: ChangeFeatureDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ChangeFeatureDateView
)(implicit appConfig: AppConfig
) extends FrontendBaseController
  with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(assessmentId: AssessmentId): Action[AnyContent] = (identify andThen getData) {
    implicit request =>
      Ok(view(form, assessmentId, request.property.addressFull, createDefaultNavBar()))
  }

  def onSubmit(assessmentId: AssessmentId): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, assessmentId, request.property.addressFull, createDefaultNavBar()))),
        value =>
          Future.successful {
            val redirectUrl = if (value)
              s"${appConfig.ngrPhysicalStartUrl}/when-complete-change/${assessmentId.value}"
            else
              s"${appConfig.ngrPhysicalStartUrl}/have-you-changed-use-of-space/${assessmentId.value}"
            Redirect(redirectUrl)
          }
      )
  }
}
