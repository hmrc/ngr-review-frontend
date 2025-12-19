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

package config

import com.google.inject.{Inject, Singleton}
import config.features.Features
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val registrationUrl: String
  val dashboardUrl: String
  val ngrLogoutUrl: String
  val nextGenerationRatesUrl: String
  val nextGenerationRatesNotifyUrl: String
  val features: Features
}

@Singleton
class FrontendAppConfig @Inject()(configuration: Configuration, sc: ServicesConfig) extends AppConfig {

  val host: String = configuration.get[String]("host")
  val appName: String = configuration.get[String]("appName")

  private val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "ngr-review-frontend"

  def feedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier&backUrl=${host + request.uri}"

  val loginUrl: String = configuration.get[String]("urls.login")
  val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  val signOutUrl: String = configuration.get[String]("urls.signOut")

  val dashboardHost: String = getString("microservice.services.ngr-dashboard-frontend.host")
  val registrationHost: String = getString("microservice.services.ngr-login-register-frontend.host")
  override val dashboardUrl: String = s"$dashboardHost/ngr-dashboard-frontend/dashboard"
  override val ngrLogoutUrl: String = s"$dashboardHost/ngr-dashboard-frontend/signout"
  override val nextGenerationRatesUrl: String = sc.baseUrl("next-generation-rates")
  override val nextGenerationRatesNotifyUrl: String = sc.baseUrl("ngr-notify")

  override val features = new Features()(configuration)

  override val registrationUrl: String = s"$registrationHost/ngr-login-register-frontend/register"

  private val exitSurveyBaseUrl: String = configuration.get[Service]("microservice.services.feedback-frontend").baseUrl
  val exitSurveyUrl: String = s"$exitSurveyBaseUrl/feedback/ngr-review-frontend"

  val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en"),
    "cy" -> Lang("cy")
  )

  val timeout: Int = configuration.get[Int]("timeout-dialog.timeout")
  val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  val cacheTtl: Long = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  private def getString(key: String): String =
    configuration.getOptional[String](key).filter(!_.isBlank).getOrElse(throwConfigNotFoundError(key))

  private def throwConfigNotFoundError(key: String): String =
    throw new RuntimeException(s"Could not find config key '$key'")

}
