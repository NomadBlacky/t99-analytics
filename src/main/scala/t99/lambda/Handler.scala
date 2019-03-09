package t99.lambda

import java.util

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class Handler(private val validAuthToken: String) extends RequestHandler[Request, Response] {

  /**
    * Default constructor for Lambda.
    */
  def this() = this(sys.env("AUTH_TOKEN"))

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
      }.andThen {
        case Success(value) =>
          logger.log(s"OK: $value")
        case Failure(exception) =>
          logger.log(s"Error: $exception")
      }
    }

    Await.result(resultF, Duration.Inf)
  }

  private def checkToken(token: String): Future[Unit] = {
    if (token == validAuthToken) Future.successful(Unit)
    else Future.failed(InvalidTokenException())
  }

  private def handleInternal(body: RequestBody, context: Context): Future[Response] = {
//    for {
//      tweet <- client.getTweet(body.tweetId)
//    } yield {
//      Response(
//        200,
//        tweet.toString,
//        new util.HashMap(),
//      )
//    }
    Future.successful(Response(200, "OK", new util.HashMap()))
  }
}

case class InvalidTokenException() extends Throwable
