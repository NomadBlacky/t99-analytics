package t99.twitter.model
import java.time.Instant

import org.scalatest.{FunSpec, MustMatchers}
import upickle.default._

import scala.io.Source

class TweetSpec extends FunSpec with MustMatchers {

  describe("tweetReader") {
    it("should be able to parse tweet that has media") {
      val tweetJson = Source.fromResource("sample_tweet.json").getLines().mkString("\n")
      val tweet     = read(tweetJson)(Tweet.tweetReader)

      val expect = Tweet(
        TweetId("1104057231635431424"),
        Seq(
          TweetMedia(TweetMediaType.Photo, "https://pbs.twimg.com/media/D1JmM2hVsAAxA4O.jpg"),
          TweetMedia(TweetMediaType.Photo, "https://pbs.twimg.com/media/D1JmM2XUYAABN9B.jpg")
        ),
        Instant.parse("2019-03-08T16:32:11Z")
      )

      tweet mustBe expect
    }

    it("should be able to parse tweet that has no media") {
      val tweetJson = Source.fromResource("sample_tweet_no_extended_entities.json").getLines().mkString("\n")
      val tweet     = read(tweetJson)(Tweet.tweetReader)

      val expect = Tweet(TweetId("1104057231635431424"), Seq.empty, Instant.parse("2019-03-08T16:32:11Z"))

      tweet mustBe expect
    }
  }
}
