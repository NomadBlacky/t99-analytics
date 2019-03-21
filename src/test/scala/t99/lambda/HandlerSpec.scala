package t99.lambda
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.{ClientContext, CognitoIdentity, Context, LambdaLogger}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.{AsyncFunSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import t99.rekognition.{DetectedTextResults, RekognitionClient, T99Image}
import t99.results.T99ResultValue._
import t99.results.{T99Result, T99ResultExtractor}
import t99.twitter.TwitterClient
import t99.twitter.model.Tweet

import scala.collection.JavaConverters._
import scala.concurrent.Future

class HandlerSpec extends AsyncFunSpec with MustMatchers with MockitoSugar {

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

      val expect = Response(
        401,
        """{"error":"Invalid token"}""",
        Map("Content-Type" -> "application/json").asJava
      )

      result mustBe expect
    }

    it("must return a 200 response when authToken is valid") {
      val t99Result = T99Result(
        ko = Some(KO(1)),
        exp = Some(Exp(2)),
        single = Some(Single(3)),
        double = Some(Double(4)),
        triple = Some(Triple(5)),
        tetris = Some(Tetris(6)),
        tSpin = Some(TSpin(7)),
        miniTSpin = Some(MiniTSpin(8)),
        tSpinSingle = Some(TSpinSingle(9)),
        tSpinDouble = Some(TSpinDouble(10)),
        tSpinTriple = Some(TSpinTriple(11)),
        maxRen = Some(MaxRen(12)),
        backToBack = Some(BackToBack(13)),
        allClear = None
      )
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
          when(m.extract(any())).thenReturn(t99Result)
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

      result mustBe Response(
        200,
        """{"ko":[1],"exp":[2],"single":[3],"double":[4],"triple":[5],"tetris":[6],"t_spin":[7],"mini_t_spin":[8],"t_spin_single":[9],"t_spin_double":[10],"t_spin_triple":[11],"max_ren":[12],"back_to_back":[13],"all_clear":[]}""",
        Map("Content-Type" -> "application/json").asJava
      )
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
