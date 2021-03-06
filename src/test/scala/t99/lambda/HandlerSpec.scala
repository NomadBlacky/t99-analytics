package t99.lambda
import java.time.Instant

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import org.mockito.Answers
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.{AsyncFunSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse
import t99.dynamodb.T99DynamoDbClient
import t99.rekognition.{DetectedTextResults, RekognitionClient, T99Image}
import t99.results.T99ResultValueType._
import t99.results.{T99Result, T99ResultExtractor, T99ResultValue}
import t99.twitter.TwitterClient
import t99.twitter.model.{Tweet, TweetId, TweetMedia, TweetMediaType}

import scala.collection.JavaConverters._
import scala.concurrent.Future

class HandlerSpec extends AsyncFunSpec with MustMatchers with MockitoSugar {

  describe("handleRequest") {

    it("must return a 401 response when authToken is invalid") {
      val handler = new Handler(
        "debug",
        "valid token",
        mock[TwitterClient],
        mock[RekognitionClient],
        mock[T99ResultExtractor],
        mock[T99DynamoDbClient]
      )
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
        Seq(
          T99ResultValue(Ko, 1),
          T99ResultValue(Exp, 2),
          T99ResultValue(Single, 3),
          T99ResultValue(Double, 4),
          T99ResultValue(Triple, 5),
          T99ResultValue(Tetris, 6),
          T99ResultValue(TSpin, 7),
          T99ResultValue(MiniTSpin, 8),
          T99ResultValue(TSpinSingle, 9),
          T99ResultValue(TSpinDouble, 10),
          T99ResultValue(TSpinTriple, 11),
          T99ResultValue(MaxRen, 12),
          T99ResultValue(BackToBack, 13),
        )
      )
      val handler = {
        val mockTwitterClient = {
          val m = mock[TwitterClient]
          when(m.getTweet(any())(any())).thenReturn {
            Future.successful(
              Tweet(
                TweetId("1104410671842713601"),
                Seq(TweetMedia(TweetMediaType.Photo, "https://tw.example.com/12345")),
                Instant.ofEpochMilli(1557659372L)
              )
            )
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
        val mockDynamoDbClient = {
          val m   = mock[T99DynamoDbClient]
          val res = PutItemResponse.builder().build()
          when(m.putResult(any(), any())).thenReturn(Future.successful(res))
          m
        }
        new Handler("debug", "valid token", mockTwitterClient, mockRekognitionClient, mockExtractor, mockDynamoDbClient)
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
        """{"t_spin_single":9,"t_spin_double":10,"tetris":6,"triple":5,"ko":1,"exp":2,"max_ren":12,"double":4,"single":3,"t_spin_triple":11,"mini_t_spin":8,"back_to_back":13,"t_spin":7}""",
        Map("Content-Type" -> "application/json").asJava
      )
    }

    it("must return a 200 response when images not found in requested tweet") {
      val handler = {
        val mockTwitterClient = {
          val tweet = Tweet(
            TweetId("1104410671842713601"),
            Seq(TweetMedia(TweetMediaType.Video, "https://tw.example.com/12345")),
            Instant.ofEpochSecond(1557659372L)
          )
          val m = mock[TwitterClient](defaultAnswer = Answers.RETURNS_SMART_NULLS)
          when(m.getTweet(any())(any())).thenReturn {
            Future.successful(tweet)
          }
          m
        }
        new Handler(
          "debug",
          "valid token",
          mockTwitterClient,
          mock[RekognitionClient],
          mock[T99ResultExtractor],
          mock[T99DynamoDbClient]
        )
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
        s"""{"message":"Tweet 1104410671842713601 has no images."}""",
        Map("Content-Type" -> "application/json").asJava
      )
    }
  }
}
