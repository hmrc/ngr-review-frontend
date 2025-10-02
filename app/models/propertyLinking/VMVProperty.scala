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

import play.api.libs.json.{Format, Json, OFormat}

import java.time.LocalDate

case class VMVProperty(uarn: Long,
                       addressFull: String,
                       localAuthorityCode: String,
                       localAuthorityReference: String,
                       valuations: List[Valuation]
                      )

object VMVProperty {
  implicit val format: OFormat[VMVProperty] = Json.format[VMVProperty]
}

case class Valuation(
                      assessmentRef: Long,
                      assessmentStatus: String,
                      rateableValue: Option[BigDecimal],
                      scatCode: Option[String],
                      descriptionText: String,
                      effectiveDate: LocalDate,
                      currentFromDate: LocalDate,
                      listYear: String,
                      primaryDescription: String,
                      allowedActions: List[String],
                      listType: String,
                      propertyLinkEarliestStartDate: Option[LocalDate]
                    )

object Valuation {

  implicit val format:Format[Valuation] = Json.format[Valuation]

}