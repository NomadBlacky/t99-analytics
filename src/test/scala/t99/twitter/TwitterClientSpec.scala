package t99.twitter
import java.time.Instant

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.{AsyncFunSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import scalaj.http._
import t99.twitter.model.{Tweet, TweetId, TweetMedia}

import scala.io.Source

class TwitterClientSpec extends AsyncFunSpec with MustMatchers with MockitoSugar {

  def genTwitterClient(mockHttpClient: HttpClient): TwitterClient =
    new TwitterClient("oatuh2bearertoken", mockHttpClient)

  describe("getTweet") {
    def genHttpClientMock(responseBody: String): HttpClient = {
      val mockClient = mock[HttpClient]
      val mockResponse = {
        val m = mock[HttpResponse[String]]
        when(m.body).thenReturn(responseBody)
        m
      }
      when(mockClient.executeString(any())).thenReturn(mockResponse)
      mockClient
    }

    it("should return a tweet") {
      val client = genTwitterClient(
        genHttpClientMock(Source.fromResource("sample_tweet.json").getLines().mkString("\n"))
      )
      val result = client.getTweet(TweetId("1104057231635431424"))

      val expect = Tweet(
        TweetId("1104057231635431424"),
        Seq(
          TweetMedia("photo", "https://pbs.twimg.com/media/D1JmM2hVsAAxA4O.jpg"),
          TweetMedia("photo", "https://pbs.twimg.com/media/D1JmM2XUYAABN9B.jpg")
        ),
        Instant.parse("2019-03-08T16:32:11Z")
      )

      result.map(_ mustBe expect)
    }
  }

}
