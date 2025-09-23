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

package models.propertyLinking

import models.registration.CredId
import play.api.libs.json.{Format, Json}

case class PropertyLinkingUserAnswers (credId: CredId,
                                       vmvProperty: VMVProperty,
                                       currentRatepayer: Option[CurrentRatepayer] = None,
                                       businessRatesBill: Option[String] = None,
                                       connectionToProperty: Option[String] = None,
                                       requestSentReference: Option[String] = None,
                                       evidenceDocument: Option[String] = None
                                      )

object PropertyLinkingUserAnswers {
  
  implicit val format:Format[PropertyLinkingUserAnswers] = Json.format[PropertyLinkingUserAnswers]
  
}
