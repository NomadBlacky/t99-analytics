package t99.twitter
import java.time.Instant

import org.scalatest.{AsyncFunSpec, MustMatchers}
import scalaj.http.Token
import t99.twitter.model.{Tweet, TweetId, TweetMedia}

class TwitterClientIntegrationTest extends AsyncFunSpec with MustMatchers {

  describe("getTweet") {
    it("should request to Twitter API and return a tweet") {
      val client = new TwitterClient(
        Token(sys.env("T99_TWITTER_CONSUMER_KEY"), sys.env("T99_TWITTER_CONSUMER_SECRET")),
        Token(sys.env("T99_TWITTER_ACCESS_KEY"), sys.env("T99_TWITTER_ACCESS_SECRET"))
      )
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
