package t99.lambda
import java.awt.image.BufferedImage
import java.util

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.{ClientContext, CognitoIdentity, Context, LambdaLogger}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.{AsyncFunSpec, Inside, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import t99.rekognition.{DetectedTextResults, RekognitionClient, T99Image}
import t99.results.T99ResultValue.KO
import t99.results.{T99Result, T99ResultExtractor}
import t99.twitter.TwitterClient
import t99.twitter.model.Tweet

import scala.concurrent.Future

class HandlerSpec extends AsyncFunSpec with MustMatchers with MockitoSugar with Inside {

  describe("handleRequest") {

    it("must return a 401 response when authToken is invalid") {
      val handler = new Handler("valid token", mock[TwitterClient], mock[RekognitionClient], mock[T99ResultExtractor])
      val body =
        """{
          |  "auth_token":"invalid token",
          |  "tweet_url":"https://twitter.com/blac_k_ey/status/1104410671842713601"
          |}
        """.stripMargin
      val request = new APIGatewayProxyRequestEvent().withBody(body)

      val result = handler.handleRequest(request, TestContext())

      val expect = Response(401, "Invalid token", new util.HashMap())

      result mustBe expect
    }

    it("must return a 200 response when authToken is valid") {
      val handler = {
        val mockTwitterClient = {
          val m = mock[TwitterClient]
          when(m.getTweet(any())(any())).thenReturn {
            Future.successful(mock[Tweet])
          }
          when(m.getImages(any())(any())).thenReturn {
            Future.successful(Seq(mock[T99Image], mock[T99Image]))
          }
          m
        }
        val mockRekognitionClient = {
          val m       = mock[RekognitionClient]
          val results = spy(DetectedTextResults(Seq.empty))
          when(results.toString).thenReturn("OK")
          when(m.detectTexts(any())(any())).thenReturn(Future.successful(results))
          m
        }
        val mockExtractor = {
          val m = mock[T99ResultExtractor]
          when(m.extract(any())).thenReturn(T99Result(ko = Option(KO(20))))
          m
        }
        new Handler("valid token", mockTwitterClient, mockRekognitionClient, mockExtractor)
      }
      val body =
        """{
          |  "auth_token":"valid token",
          |  "tweet_url":"https://twitter.com/blac_k_ey/status/1104410671842713601"
          |}
        """.stripMargin
      val request = new APIGatewayProxyRequestEvent().withBody(body)

      val result = handler.handleRequest(request, TestContext())

      inside(result) {
        case Response(statusCode, _, headers) =>
          statusCode mustBe 200
          headers mustBe new util.HashMap()
      }
    }
  }
}

case class TestContext() extends Context with MockitoSugar {
  def getAwsRequestId: String         = "requestId"
  def getLogGroupName: String         = "logGroupName"
  def getLogStreamName: String        = "logStreamName"
  def getFunctionName: String         = "functionName"
  def getFunctionVersion: String      = "functionVersion"
  def getInvokedFunctionArn: String   = "invokedFunctionArn"
  def getIdentity: CognitoIdentity    = mock[CognitoIdentity]
  def getClientContext: ClientContext = mock[ClientContext]
  def getRemainingTimeInMillis: Int   = 1000
  def getMemoryLimitInMB: Int         = 1024
  def getLogger: LambdaLogger = new LambdaLogger {
    override def log(message: String): Unit      = println(message)
    override def log(message: Array[Byte]): Unit = println(message)
  }
}
