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
import models.AssessmentId
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.DeclarationView

class DeclarationViewSpec extends ViewBaseSpec {

  val assessmentId = AssessmentId("85141561000L")

  val view: DeclarationView = app.injector.instanceOf[DeclarationView]
  val address: String = "123 Street Lane"

  object Selectors {
    val firstParagraph = "#para-1"
    val continue = "#continue"


  }

  "Declaration view" must {
    val declarationView = view(assessmentId, address, navBarContent())
    lazy implicit val document: Document = Jsoup.parse(declarationView.body)


    "show correct header" in {
      elementText("h1") mustBe "Declaration"
    }

    "show correct " in {
      elementText(Selectors.firstParagraph) mustBe "By submitting these details, you declare that to the best of your knowledge the information you have given is correct and complete."
    }

    "show correct accept and send" in {
      elementText(Selectors.continue) mustBe "Accept and send"
    }

  }

}
