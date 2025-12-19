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

package actions

import base.SpecBase
import config.FrontendAppConfig
import connectors.NGRConnector
import controllers.actions.{AuthenticatedIdentifierAction, IdentifierAction}
import controllers.routes
import models.registration.{CredId, RatepayerRegistration, RatepayerRegistrationValuation}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.inject
import play.api.inject.bind
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class AuthActionSpec extends SpecBase with BeforeAndAfterEach {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }

  override def beforeEach(): Unit = {
    reset(mockNGRConnector, mockAppConfig, testBodyParser)
    super.beforeEach()
  }

  val testBodyParser: BodyParsers.Default = mock[BodyParsers.Default]
  val mockNGRConnector: NGRConnector = mock[NGRConnector]
  val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  "Auth Action" - {

    "when the user is logged in" - {
      "must succeed and return Ok" in {

        type AuthRetrievals = Option[Credentials] ~ Option[String] ~ ConfidenceLevel

        val application = applicationBuilder(userAnswers = None)
          .overrides(
            bind[NGRConnector].toInstance(mockNGRConnector),
            bind[BodyParsers.Default].toInstance(testBodyParser))
          .build()

        val registeredRatepayer: RatepayerRegistrationValuation = RatepayerRegistrationValuation(CredId("1234"), Some(RatepayerRegistration(isRegistered = Some(true))))

        when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(Some(registeredRatepayer)))

        running(application) {
          val mockAuthConnector: AuthConnector = mock[AuthConnector]
          val retrieval: AuthRetrievals = Some(Credentials("id", "provider")) ~ Some("id") ~ ConfidenceLevel.L250

          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())).thenReturn(Future.successful(
            retrieval
          ))

          val action = new AuthenticatedIdentifierAction(mockAuthConnector, mockNGRConnector, mockAppConfig, testBodyParser)
          val controller = new Harness(action)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe OK
        }
      }

      "must redirect to registration login service" in {
        type AuthRetrievals = Option[Credentials] ~ Option[String] ~ ConfidenceLevel
        lazy val testBodyParser: BodyParsers.Default = mock[BodyParsers.Default]
        val mockNGRConnector: NGRConnector = mock[NGRConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(
            bind[NGRConnector].toInstance(mockNGRConnector),
            bind[BodyParsers.Default].toInstance(testBodyParser))
          .build()

        val emptyRatepayer: RatepayerRegistrationValuation = RatepayerRegistrationValuation(CredId("1234"), None)

        when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(Some(emptyRatepayer)))

        running(application) {
          val frontendAppConfig = application.injector.instanceOf[FrontendAppConfig]
          val mockAuthConnector: AuthConnector = mock[AuthConnector]
          val retrieval: AuthRetrievals = Some(Credentials("id", "provider")) ~ Some("id") ~ ConfidenceLevel.L500

          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())).thenReturn(Future.successful(
            retrieval
          ))

          val action = new AuthenticatedIdentifierAction(mockAuthConnector, mockNGRConnector, frontendAppConfig, testBodyParser)
          val controller = new Harness(action)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some("http://localhost:1502/ngr-login-register-frontend/register")
        }
      }

      "must throw an exception if the confidence level is not met" in {

        type AuthRetrievals = Option[Credentials] ~ Option[String] ~ ConfidenceLevel

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val testBodyParser = application.injector.instanceOf[BodyParsers.Default]
          val mockAuthConnector: AuthConnector = mock[AuthConnector]
          val retrieval: AuthRetrievals = Some(Credentials("id", "provider")) ~ None ~ ConfidenceLevel.L50

          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())).thenReturn(Future.successful(
            retrieval
          ))

          val action = new AuthenticatedIdentifierAction(mockAuthConnector, mockNGRConnector, mockAppConfig, testBodyParser)
          val controller = new Harness(action)

          val results = intercept[Exception] {
            await(controller.onPageLoad()(FakeRequest("", "")))
          }
          results.getMessage mustBe "confidenceLevel not met expected L250 but was 50"
        }
      }
    }

    "when the user hasn't logged in" - {

      "must redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), mockNGRConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must startWith(appConfig.dashboardUrl)
        }
      }
    }

    "the user's session has expired" - {

      "must redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), mockNGRConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must startWith(appConfig.dashboardUrl)
        }
      }
    }

    "the user doesn't have sufficient enrolments" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments), mockNGRConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad().url
        }
      }
    }

    "the user doesn't have sufficient confidence level" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientConfidenceLevel), mockNGRConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad().url
        }
      }
    }

    "the user used an unaccepted auth provider" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider), mockNGRConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad().url
        }
      }
    }

    "the user has an unsupported affinity group" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAffinityGroup), mockNGRConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
        }
      }
    }

    "the user has an unsupported credential role" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedCredentialRole), mockNGRConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
        }
      }
    }
  }
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
