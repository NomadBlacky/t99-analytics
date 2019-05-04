package t99.twitter
import java.time.Instant

import com.amazonaws.xray.AWSXRay
import org.scalatest.{AsyncFunSpec, BeforeAndAfterAll, MustMatchers}
import t99.twitter.model.{Tweet, TweetId, TweetMedia}

class TwitterClientIntegrationTest extends AsyncFunSpec with MustMatchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    AWSXRay.getGlobalRecorder.beginSegment("Dummy segment")
  }

  override protected def afterAll(): Unit = {
    AWSXRay.getGlobalRecorder.endSegment()
  }

  describe("getTweet") {
    it("should request to Twitter API and return a tweet") {
      val client  = new TwitterClient(sys.env("T99_TWITTER_OAUTH2_BEARER_TOKEN"))
      val tweetId = TweetId("1104057231635431424")

      val expect = Tweet(
        tweetId,
        Seq(
          TweetMedia("photo", "https://pbs.twimg.com/media/D1JmM2hVsAAxA4O.jpg"),
          TweetMedia("photo", "https://pbs.twimg.com/media/D1JmM2XUYAABN9B.jpg")
        ),
        Instant.parse("2019-03-08T16:32:11Z")
      )

      val actual = client.getTweet(tweetId)

      actual.map(_ mustBe expect)
    }
  }

}
