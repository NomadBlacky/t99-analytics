package t99.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder
import com.github.j5ik2o.reactive.aws.dynamodb.DynamoDbAsyncClient
import org.apache.logging.log4j.LogManager
import software.amazon.awssdk.services.dynamodb.{DynamoDbAsyncClient => JavaDynamoDbAsyncClient}
import t99.dynamodb.T99DynamoDbClient
import t99.lambda.RequestHelper._
import t99.rekognition.RekognitionClient
import t99.results.T99ResultExtractor
import t99.twitter.TwitterClient
import utils.SnakePickle

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class Handler(
    validAuthToken: String,
    twitterClient: TwitterClient,
    rekognitionClient: RekognitionClient,
    resultExtractor: T99ResultExtractor,
    dynamoDbClient: T99DynamoDbClient
) extends RequestHandler[APIGatewayProxyRequestEvent, Response] {

  private val logger = LogManager.getLogger(getClass)

  /**
    * Default constructor for Lambda.
    */
  def this() = this(
    sys.env("AUTH_TOKEN"),
    new TwitterClient(sys.env("TWITTER_OAUTH2_BEARER_TOKEN")),
    new RekognitionClient(AmazonRekognitionClientBuilder.defaultClient()),
    new T99ResultExtractor,
    new T99DynamoDbClient(DynamoDbAsyncClient(JavaDynamoDbAsyncClient.create()), sys.env("RESULT_TABLE_NAME"))
  )

  def handleRequest(input: APIGatewayProxyRequestEvent, context: Context): Response = {
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

  private def handleInternal(body: RequestBody, context: Context): Future[Response] =
    for {
      tweet           <- twitterClient.getTweet(body.tweetId)
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
}

case class InvalidTokenException() extends Throwable
