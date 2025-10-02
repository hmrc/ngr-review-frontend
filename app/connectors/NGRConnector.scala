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

package connectors

import config.{AppConfig, FrontendAppConfig}
import models.propertyLinking.{PropertyLinkingUserAnswers, VMVProperty}
import models.registration.{CredId, RatepayerRegistrationValuation}
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, NotFoundException, StringContextOps}

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NGRConnector @Inject()(http: HttpClientV2,
                             appConfig: AppConfig,
                            )
                            (implicit ec: ExecutionContext) {
  private def url(path: String): URL = url"${appConfig.nextGenerationRatesUrl}/next-generation-rates/$path"

  def getRatepayer(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[RatepayerRegistrationValuation]] = {
    implicit val rds: HttpReads[RatepayerRegistrationValuation] = readFromJson
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, None)
    http.get(url("get-ratepayer"))
      .withBody(Json.toJson(model))
      .execute[Option[RatepayerRegistrationValuation]]
  }

  def getPropertyLinkingUserAnswers(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[PropertyLinkingUserAnswers]] = {
    implicit val rds: HttpReads[PropertyLinkingUserAnswers] = readFromJson
    val dummyVMVProperty: VMVProperty = VMVProperty(0L, "", "", "", List.empty) // TODO: Replace with a proper VMVProperty instance if needed
    val model: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId, dummyVMVProperty)
    http.get(url("get-property-linking-user-answers"))
      .withBody(Json.toJson(model))
      .execute[Option[PropertyLinkingUserAnswers]]
  }

  def getLinkedProperty(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[VMVProperty]] = {
    getPropertyLinkingUserAnswers(credId)
      .map {
        case Some(propertyLinkingUserAnswers) => Some(propertyLinkingUserAnswers.vmvProperty)
        case None => throw new NotFoundException("failed to find propertyLinkingUserAnswers from backend mongo")
      }
  }

}
