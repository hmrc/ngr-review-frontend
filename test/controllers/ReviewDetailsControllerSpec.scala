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

import base.SpecBase
import config.FrontendAppConfig
import connectors.NGRNotifyConnector
import helpers.TestData
import models.NavBarPageContents.createDefaultNavBar
import models.{AssessmentId, LevelSummary, PhysicalDetails, ReviewDetails}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, PrivateMethodTester}
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Messages
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.{Application, inject}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.Section
import viewmodels.govuk.all.{SummaryListRowViewModel, ValueViewModel, stringToKey}
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*
import views.html.ReviewDetailsView

import scala.concurrent.Future

class ReviewDetailsControllerSpec extends SpecBase with PrivateMethodTester with BeforeAndAfterEach with TestData {

  val assessmentId                               = AssessmentId("test-assessment-id")
  val mockNgrNotifyConnector: NGRNotifyConnector = mock[NGRNotifyConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockNgrNotifyConnector)
  }

  def buildTestApplication(): Application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
    .overrides(
      inject.bind[NGRNotifyConnector].toInstance(mockNgrNotifyConnector)
    )
    .build()

  "ReviewDetails Controller" - {

    "must return OK and the correct view for a GET" in {
      val application: Application   = buildTestApplication()
      implicit val message: Messages = messages(application)
      val totalAreaRow               = SummaryListRowViewModel(
        key = "reviewDetails.total.area",
        value = ValueViewModel("100.00"),
        actions = Seq.empty
      )

      val sections = Seq(Section(None, None, SummaryListViewModel(Seq(totalAreaRow)).withCssClass("govuk-summary-list--no-border")))

      val sampleResponse: ReviewDetails =
        ReviewDetails(floorsInfo = List.empty, otherAdditionInfo = List.empty, parkingInfo = List.empty, totalArea = 100, fullAddress = None)

      when(mockNgrNotifyConnector.getReviewDetails(any[AssessmentId])(using any[HeaderCarrier]))
        .thenReturn(
          Future.successful(Some(sampleResponse))
        )

      implicit val appConfig: FrontendAppConfig = application.injector.instanceOf[FrontendAppConfig]
      running(application) {
        val request = FakeRequest(GET, routes.ReviewDetailsController.onPageLoad(assessmentId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ReviewDetailsView]

        status(result)               mustEqual OK
        contentAsString(result).trim mustEqual view(createDefaultNavBar(), property.addressFull, assessmentId, sections)(
          request,
          messages(application),
          appConfig
        ).toString.trim
      }
    }

    "must return InternalServerError when review details are not found" in {
      val application: Application = buildTestApplication()
      when(mockNgrNotifyConnector.getReviewDetails(any[AssessmentId])(using any[HeaderCarrier]))
        .thenReturn(
          Future.successful(None)
        )

      running(application) {
        val request = FakeRequest(GET, routes.ReviewDetailsController.onPageLoad(assessmentId).url)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "createSectionList" - {
      "generate correct sections from ReviewDetails" in {
        val application: Application    = buildTestApplication()
        implicit val messages: Messages = stubMessages()
        val controller                  = application.injector.instanceOf[ReviewDetailsController]

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

        val createSection          = PrivateMethod[Seq[Section]](Symbol("createSectionList"))
        val sections: Seq[Section] = controller.invokePrivate(createSection(sampleReviewDetails, messages))
        sections.size                              mustBe 7
        sections.head.rows.rows.size               mustBe 1
        sections.head.rows.rows.head.key.content   mustBe Text("reviewDetails.total.area")
        sections.head.rows.rows.head.value.content mustBe Text("123.45")
        sections(1).title                          mustBe Some("first_floor")
        sections(2).rows.rows.size                 mustBe 2
        sections(2).rows.rows.head.key.content     mustBe Text("Office Space")
        sections(2).rows.rows.head.value.content   mustBe HtmlContent("50m<sup>2</sup>")
        sections(3).title                          mustBe Some("second_floor")
        sections(4).rows.rows.size                 mustBe 2
        sections(4).rows.rows.head.key.content     mustBe Text("Office Space")
        sections(4).rows.rows.head.value.content   mustBe HtmlContent("50m<sup>2</sup>")
        sections(5).title                          mustBe Some("reviewDetails.other.additions")
        sections(5).rows.rows.size                 mustBe 2
        sections(5).rows.rows.head.key.content     mustBe Text("Office Space")
        sections(5).rows.rows.head.value.content   mustBe HtmlContent("50m<sup>2</sup>")
        sections(6).title                          mustBe Some("reviewDetails.parking")
        sections(6).rows.rows.size                 mustBe 2
        sections(6).rows.rows.head.key.content     mustBe Text("Office Space")
        sections(6).rows.rows.head.value.content   mustBe HtmlContent("50m<sup>2</sup>")
      }

      "generate empty sections when ReviewDetails has no floors, other additions or parking info" in {
        val application: Application    = buildTestApplication()
        implicit val messages: Messages = stubMessages()
        val controller                  = application.injector.instanceOf[ReviewDetailsController]

        val sampleReviewDetails = ReviewDetails(
          floorsInfo = List.empty,
          otherAdditionInfo = List.empty,
          parkingInfo = List.empty,
          totalArea = BigDecimal(0),
          fullAddress = Some("some address")
        )

        val createSection          = PrivateMethod[Seq[Section]](Symbol("createSectionList"))
        val sections: Seq[Section] = controller.invokePrivate(createSection(sampleReviewDetails, messages))
        sections.size                              mustBe 1
        sections.head.rows.rows.size               mustBe 1
        sections.head.rows.rows.head.key.content   mustBe Text("reviewDetails.total.area")
        sections.head.rows.rows.head.value.content mustBe Text("0.00")
      }
    }

  }
}
