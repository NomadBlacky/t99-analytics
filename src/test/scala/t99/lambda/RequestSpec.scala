package t99.lambda
import org.scalatest.{FunSpec, MustMatchers}
import t99.twitter.model.TweetId

import scala.util.Success

class RequestSpec extends FunSpec with MustMatchers {

  describe("parsedBody") {
    it("should be able to parse a valid body") {
      val body =
        """{
          |  "auth_token":"token",
          |  "tweet_url":"https://twitter.com/blac_k_ey/status/1104410671842713601"
          |}
        """.stripMargin
      val request = Request(body)

      val expect = RequestBody(
        "token",
        "https://twitter.com/blac_k_ey/status/1104410671842713601"
      )

      request.parsedBody mustBe Success(expect)
    }
  }
}

class RequestBodySpec extends FunSpec with MustMatchers {

  describe("tweetId") {
    it("should be able to extract TweetId from a tweetUrl") {
      val body =
        RequestBody("token", "https://twitter.com/blac_k_ey/status/1104410671842713601")

      body.tweetId mustBe TweetId("1104410671842713601")
    }
  }
}
