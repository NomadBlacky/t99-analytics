package t99.lambda
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import t99.twitter.model.TweetId
import utils.SnakePickle

import scala.util.Try

case class Request(body: String) extends APIGatewayProxyRequestEvent {
  def this() = this("")
  setBody(body)

  lazy val parsedBody: Try[RequestBody] = Try(SnakePickle.read[RequestBody](body))
}

case class RequestBody(authToken: String, tweetUrl: String) {
  lazy val tweetId: TweetId = TweetId(tweetUrl.split("/").last)
}

object RequestBody {
  implicit val read: SnakePickle.Reader[RequestBody] = SnakePickle.macroR[RequestBody]
}
