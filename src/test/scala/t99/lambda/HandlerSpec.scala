package t99.lambda
import java.util

import com.amazonaws.services.lambda.runtime.Context
import org.scalatest.{FunSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar

import scala.collection.JavaConverters

class HandlerSpec extends FunSpec with MustMatchers with MockitoSugar {

  describe("handleRequest") {

    val handler = new Handler("valid token")

    it("must return a 401 response when authToken is invalid") {
      val result = handler.handleRequest(
        new Request("invalid token"),
        mock[Context]
      )
      val expect =
        Response(401, "Invalid token", new util.HashMap(), base64Encoded = true)

      result mustBe expect
    }

    it("must return a 200 response when authToken is valid") {
      val result = handler.handleRequest(
        new Request("valid token"),
        mock[Context]
      )
      val headers = Map("x-custom-response-header" -> "my custom response header value")
      val expect =
        Response(
          200,
          "Go Serverless v1.0! Your function executed successfully!",
          JavaConverters.mapAsJavaMap[String, Object](headers),
          base64Encoded = true
        )

      result mustBe expect
    }
  }
}
