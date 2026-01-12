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

package utils

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
import viewmodels.Section
import viewmodels.govuk.all.{SummaryListRowViewModel, stringToKey}
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

object ReviewDetailsHelper {

  def createSectionList(reviewDetails: ReviewDetails)(implicit messages: Messages): Seq[Section] = {
    val areaStr                      = reviewDetails.totalArea.setScale(2, scala.math.BigDecimal.RoundingMode.HALF_UP).toString
    val totalAreaRow: SummaryListRow = SummaryListRowViewModel(
      key = "reviewDetails.total.area",
      value = ValueViewModel(HtmlContent(s"${areaStr}m<sup>2</sup>")),
      actions = Seq.empty
    )

    val totalSection = buildSection(None, None, Seq(totalAreaRow), noBorder = true)

    val parkingSpaces  = reviewDetails.parkingInfo.flatMap(_.spaces).map(spaceSummaryRow)
    val parkingSection = Option.when(parkingSpaces.nonEmpty)(
      buildSection(Some("reviewDetails.parking"), None, parkingSpaces)
    ).toSeq

    val otherSpaces           = reviewDetails.otherAdditionInfo.flatMap(_.spaces).map(spaceSummaryRow)
    val otherAdditionsSection = Option.when(otherSpaces.nonEmpty)(
      buildSection(Some("reviewDetails.other.additions"), None, otherSpaces)
    ).toSeq

    totalSection +: (reviewDetails.floorsInfo.flatMap(createSummaryList) ++ otherAdditionsSection ++ parkingSection)
  }

  private def spaceSummaryRow(space: PhysicalDetails)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = space.description,
      value = ValueViewModel(HtmlContent(s"${space.quantity.toString()}${if (space.units == "m2") "m<sup>2</sup>" else s" ${space.units}"}")),
      actions = Seq.empty
    )

  private def createSummaryList(floor: LevelSummary)(implicit messages: Messages): Seq[Section] = {

    val spaceRows: List[SummaryListRow] = floor.spaces map spaceSummaryRow

    Seq(
      buildSection(Some(floor.label.replaceAll(" ", "_").toLowerCase())),
      buildSection(None, Some("reviewDetails.floor.space"), spaceRows)
    )
  }

  private def buildSection(heading: Option[String], subHeading: Option[String] = None, rows: Seq[SummaryListRow] = Seq.empty, noBorder: Boolean = false)
    : Section =
    val base    = SummaryListViewModel(rows)
    val summary = if (noBorder) base.withCssClass("govuk-summary-list--no-border") else base
    Section(heading, subHeading, summary)
}
