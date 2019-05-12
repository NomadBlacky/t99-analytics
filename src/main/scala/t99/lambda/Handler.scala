package t99.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.github.j5ik2o.reactive.aws.dynamodb.DynamoDbAsyncClient
import com.github.j5ik2o.reactive.aws.rekognition.RekognitionAsyncClient
import org.apache.logging.log4j.LogManager
import software.amazon.awssdk.services.dynamodb.{DynamoDbAsyncClient => JavaDynamoDbAsyncClient}
import software.amazon.awssdk.services.rekognition.{RekognitionAsyncClient => JavaRekognitionAsyncClient}
import t99.dynamodb.T99DynamoDbClient
import t99.lambda.RequestHelper._
import t99.rekognition.RekognitionClient
import t99.results.T99ResultExtractor
import t99.twitter.TwitterClient
import t99.twitter.model.Tweet
import utils.SnakePickle

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class Handler(
    env: String,
    validAuthToken: String,
    twitterClient: TwitterClient,
    rekognitionClient: RekognitionClient,
    resultExtractor: T99ResultExtractor,
    dynamoDbClient: T99DynamoDbClient
) extends RequestHandler[APIGatewayProxyRequestEvent, Response] {

  private[this] val logger = LogManager.getLogger(getClass)

  /**
    * Default constructor for Lambda.
    */
  def this() = this(
    sys.env.getOrElse("ENV", "dev"),
    sys.env("AUTH_TOKEN"),
    new TwitterClient(sys.env("TWITTER_OAUTH2_BEARER_TOKEN")),
    new RekognitionClient(RekognitionAsyncClient(JavaRekognitionAsyncClient.create())),
    new T99ResultExtractor,
    new T99DynamoDbClient(DynamoDbAsyncClient(JavaDynamoDbAsyncClient.create()), sys.env("RESULT_TABLE_NAME"))
  )

  def handleRequest(input: APIGatewayProxyRequestEvent, context: Context): Response = {
    implicit val metricsLogger: MetricsLogger = MetricsLogger("t99", env, context)

    logger.info(input.toString)

    val resultF: Future[Response] = {
      val fu = for {
        body     <- Future.fromTry(input.parsedBody)
        _        <- checkToken(body.authToken)
        response <- handleInternal(body, context)
      } yield response

      fu.recover {
          case InvalidTokenException() =>
            Response(
              401,
              SnakePickle.write(Map("error" -> "Invalid token")),
              Map("Content-Type"            -> "application/json").asJava
            )
        }
        .andThen {
          case Success(value) =>
            logger.info(s"OK: $value")
          case Failure(exception) =>
            logger.error(s"Error: ${exception.getMessage}", exception)
        }
    }

    Await.result(resultF, Duration.Inf)
  }

  private def checkToken(token: String): Future[Unit] =
    if (token == validAuthToken) Future.successful(Unit)
    else Future.failed(InvalidTokenException())

  private def handleInternal(body: RequestBody, context: Context)(implicit ml: MetricsLogger): Future[Response] = {
    val fu = for {
      tweet           <- twitterClient.getTweet(body.tweetId)
      _               <- hasImages(tweet)
      images          <- twitterClient.getImages(tweet)
      detectedResults <- Future.traverse(images)(rekognitionClient.detectTexts(_))
      t99Results      <- Future.traverse(detectedResults)(r => Future.successful(resultExtractor.extract(r)))
      mergedResult    = t99Results.reduce(_ merge _)
      _               = mergedResult.sendMetrics(tweet.createdAt)
      _               <- dynamoDbClient.putResult(tweet.id, mergedResult)
      resultJson      = SnakePickle.write(mergedResult)
    } yield {
      Response(200, resultJson, Map("Content-Type" -> "application/json").asJava)
    }

    fu.recover {
      case TweetHasNoImagesException(tweet) =>
        Response(
          200,
          s"""{"message":"Tweet ${tweet.id.value} has no images."}""",
          Map("Content-Type" -> "application/json").asJava
        )
    }
  }

  private def hasImages(tweet: Tweet): Future[Unit] =
    if (tweet.photos.nonEmpty) Future.successful(())
    else Future.failed(TweetHasNoImagesException(tweet))
}

case class InvalidTokenException()                 extends Throwable
case class TweetHasNoImagesException(tweet: Tweet) extends Throwable
