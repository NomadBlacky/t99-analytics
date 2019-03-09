package t99.lambda
import java.util

import com.amazonaws.services.lambda.runtime.{ClientContext, CognitoIdentity, Context, LambdaLogger}
import org.scalatest.{FunSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar

class HandlerSpec extends FunSpec with MustMatchers with MockitoSugar {

  describe("handleRequest") {

    val handler = new Handler("valid token")

    it("must return a 401 response when authToken is invalid") {
      val result = handler.handleRequest(
        Request(
          """{
            |  "auth_token":"invalid token",
            |  "tweet_url":"https://twitter.com/blac_k_ey/status/1104410671842713601"
            |}
          """.stripMargin
        ),
        TestContext()
      )

      val expect = Response(401, "Invalid token", new util.HashMap())

      result mustBe expect
    }

    it("must return a 200 response when authToken is valid") {
      val result = handler.handleRequest(
        Request(
          """{
            |  "auth_token":"valid token",
            |  "tweet_url":"https://twitter.com/blac_k_ey/status/1104410671842713601"
            |}
          """.stripMargin
        ),
        TestContext()
      )

      val expect = Response(200, "OK", new util.HashMap())

      result mustBe expect
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
