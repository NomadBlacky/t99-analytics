package t99.twitter
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream

import javax.imageio.ImageIO
import scalaj.http._
import t99.rekognition.T99Image
import t99.twitter.model.{Tweet, TweetId}

import scala.concurrent.{ExecutionContext, Future}

class TwitterClient(consumer: Token, token: Token, http: HttpClient = HttpClient) {
  def getTweet(id: TweetId)(implicit ec: ExecutionContext): Future[Tweet] = Future {
    val request = Http("https://api.twitter.com/1.1/statuses/show.json")
      .params("id" -> id.value.toString)
      .oauth(consumer, token)
    val response = http.executeString(request)

    upickle.default.read[Tweet](response.body)
  }

  def getImages(tweet: Tweet)(implicit ec: ExecutionContext): Future[Seq[T99Image]] = {
    Future.traverse(tweet.medias) { media =>
      Future {
        val response = http.executeBytes(Http(media.mediaUrlHttps))
        val image    = ImageIO.read(new ByteArrayInputStream(response.body))
        T99Image(image)
      }
    }
  }
}

object HttpClient extends HttpClient

trait HttpClient {
  def executeString(request: HttpRequest): HttpResponse[String] =
    request.asString.throwError

  def executeBytes(request: HttpRequest): HttpResponse[Array[Byte]] =
    request.asBytes.throwError
}
