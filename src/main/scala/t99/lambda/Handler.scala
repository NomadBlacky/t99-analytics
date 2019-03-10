package t99.lambda

import java.util

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import scalaj.http.Token
import t99.twitter.TwitterClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class Handler(
    private val validAuthToken: String,
    private val client: TwitterClient
) extends RequestHandler[Request, Response] {

  /**
    * Default constructor for Lambda.
    */
  def this() = this(
    sys.env("AUTH_TOKEN"),
    new TwitterClient(
      Token(sys.env("TWITTER_CONSUMER_KEY"), sys.env("TWITTER_CONSUMER_SECRET")),
      Token(sys.env("TWITTER_ACCESS_KEY"), sys.env("TWITTER_ACCESS_SECRET"))
    )
  )

  def handleRequest(input: Request, context: Context): Response = {
    val logger = context.getLogger
    logger.log(input.toString)

    val resultF: Future[Response] = {
      val fu = for {
        body     <- Future.fromTry(input.parsedBody)
        _        <- checkToken(body.authToken)
        response <- handleInternal(body, context)
      } yield response

      fu.recover {
          case InvalidTokenException() =>
            Response(401, "Invalid token", new util.HashMap())
        }
        .andThen {
          case Success(value) =>
            logger.log(s"OK: $value")
          case Failure(exception) =>
            logger.log(s"Error: $exception")
        }
    }

    Await.result(resultF, Duration.Inf)
  }

  private def checkToken(token: String): Future[Unit] =
    if (token == validAuthToken) Future.successful(Unit)
    else Future.failed(InvalidTokenException())

  private def handleInternal(body: RequestBody, context: Context): Future[Response] =
    for {
      tweet  <- client.getTweet(body.tweetId)
      images <- client.getImages(tweet)
    } yield {
      Response(200, images.length.toString, new util.HashMap())
    }
}

case class InvalidTokenException() extends Throwable
