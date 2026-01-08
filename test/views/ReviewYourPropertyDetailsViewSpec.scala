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

package views

import helpers.ViewBaseSpec
import models.{AssessmentId, NavBarPageContents, NavigationBarContent}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.ReviewYourPropertyDetailsView

class ReviewYourPropertyDetailsViewSpec extends ViewBaseSpec {
  lazy val view: ReviewYourPropertyDetailsView = inject[ReviewYourPropertyDetailsView]
  val content: NavigationBarContent            = NavBarPageContents.createDefaultNavBar
  val address                                  = "Bug Me Not PVT LTD, RODLEY LANE, RODLEY, LEEDS, BH1 1HU"
  val assessmentId                             = AssessmentId("id")

  object Selectors {
    val homeButton = "#secondary-nav > a > span"
    val signOut    = "#secondary-nav > ul > li > a"
    val navTitle   = "head > title"
    val address    = "#main-content > div > div > span"
    val heading    = "#main-content > div > div > h1"
    val p1         = "#main-content > div > div > p:nth-child(3)"
    val p2         = "#main-content > div > div > p:nth-child(4)"
    val p3         = "#main-content > div > div > p:nth-child(5)"
    val p4         = "#main-content > div > div > p:nth-child(6)"
    val continue   = "#continue"
  }

  "ReviewYourPropertyDetailsView" must {
    val reviewYourPropertyDetailsView    = view(content, assessmentId, address)
    implicit lazy val document: Document = Jsoup.parse(reviewYourPropertyDetailsView.body)
    val htmlApply                        = view.apply(content, assessmentId, address).body
    val htmlRender                       = view.render(content, assessmentId, address, request, messages, mockConfig).body
    lazy val htmlF                       = view.f(content, assessmentId, address)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }
    "apply must be the same as render" in {
      htmlApply mustBe htmlRender
    }
    "render is not empty" in {
      htmlRender must not be empty
    }
    "show account home button" in {
      elementText(Selectors.homeButton) mustBe "Account home"
    }
    "show sign out button" in {
      elementText(Selectors.signOut) mustBe "Sign out"
    }
    "show address" in {
      elementText(Selectors.address) mustBe address
    }
    "show correct title" in {
      elementText(Selectors.navTitle) mustBe "Review your property details - Manage your business rates valuation - GOV.UK"
    }
    "show correct heading" in {
      elementText(Selectors.heading) mustBe "Review your property details"
    }
    "show body" in {
      elementText(
        Selectors.p1
      )                         mustBe "You should review the details we have about your property to make sure they are correct and change anything you need to."
      elementText(Selectors.p2) mustBe "We rely on you to make sure the information we hold about your property and your rental details is correct."
      elementText(Selectors.p3) mustBe "You should upload supporting information to help us understand the updates you tell us about."
      elementText(
        Selectors.p4
      )                         mustBe "Each file must be a PDF or image (JPG or PNG) and be smaller than 25MB. You can email larger files and other file types to us."
    }
    "show continue button" in {
      elementText(Selectors.continue) mustBe "Continue"
    }
  }
}
