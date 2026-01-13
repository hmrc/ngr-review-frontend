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

import models.{LevelSummary, PhysicalDetails, ReviewDetails}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe
import play.api.i18n.Messages
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.Section

class ReviewDetailsHelperSpec extends AnyFreeSpec {
  "createSectionList" - {
    "generate correct sections from ReviewDetails" in {
      implicit val messages: Messages = stubMessages()

      val summary: LevelSummary = LevelSummary(
        label = "First Floor",
        spaces = List(
          PhysicalDetails(description = "Office Space", quantity = BigDecimal(50), units = "m2"),
          PhysicalDetails(description = "Conference Room", quantity = BigDecimal(20), units = "m2")
        ),
        totalArea = 70
      )
      val sampleReviewDetails   = ReviewDetails(
        floorsInfo = List(summary, summary.copy(label = "Second Floor")),
        otherAdditionInfo = List(summary.copy(label = "Other Additions")),
        parkingInfo = List(summary.copy(label = "Parking")),
        totalArea = BigDecimal(123.45),
        fullAddress = Some("some address")
      )

      val sections: Seq[Section] = ReviewDetailsHelper.createSectionList(sampleReviewDetails)(messages)
      println("Sections: " + sections)
      sections.size                              mustBe 5
      sections.head.rows.rows.size               mustBe 1
      sections.head.rows.rows.head.key.content   mustBe Text("reviewDetails.total.area")
      sections.head.rows.rows.head.value.content mustBe HtmlContent("123.45m<sup>2</sup>")
      sections(1).subHeading                     mustBe Some("first_floor")
      sections(2).rows.rows.size                 mustBe 2
      sections(2).rows.rows.head.key.content     mustBe Text("Office Space")
      sections(2).rows.rows.head.value.content   mustBe HtmlContent("50m<sup>2</sup>")
      sections(2).rows.rows(1).key.content       mustBe Text("Conference Room")
      sections(2).rows.rows(1).value.content     mustBe HtmlContent("20m<sup>2</sup>")
      sections(3).subHeading                     mustBe Some("reviewDetails.other.additions")
      sections(3).rows.rows.size                 mustBe 2
      sections(3).rows.rows.head.key.content     mustBe Text("Office Space")
      sections(3).rows.rows.head.value.content   mustBe HtmlContent("50m<sup>2</sup>")
      sections(4).subHeading                     mustBe Some("reviewDetails.parking")
      sections(4).rows.rows.size                 mustBe 2
      sections(4).rows.rows.head.key.content     mustBe Text("Office Space")
      sections(4).rows.rows.head.value.content   mustBe HtmlContent("50m<sup>2</sup>")
    }

    "generate empty sections when ReviewDetails has no floors, other additions or parking info" in {
      implicit val messages: Messages = stubMessages()

      val sampleReviewDetails = ReviewDetails(
        floorsInfo = List.empty,
        otherAdditionInfo = List.empty,
        parkingInfo = List.empty,
        totalArea = BigDecimal(12),
        fullAddress = Some("some address")
      )

      val sections: Seq[Section] = ReviewDetailsHelper.createSectionList(sampleReviewDetails)(messages)
      sections.size                              mustBe 1
      sections.head.rows.rows.size               mustBe 1
      sections.head.rows.rows.head.key.content   mustBe Text("reviewDetails.total.area")
      sections.head.rows.rows.head.value.content mustBe HtmlContent("12.00m<sup>2</sup>")

    }
  }
}
