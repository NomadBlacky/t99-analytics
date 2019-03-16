package t99.twitter
import java.io.ByteArrayInputStream

import javax.imageio.ImageIO
import scalaj.http._
import t99.rekognition.T99Image
import t99.results.T99Result
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

  def postTweet(tweetId: TweetId, result: T99Result)(implicit ec: ExecutionContext): Future[Unit] = Future {
    val text =
      s"""KO : ${result.ko.map(_.value).getOrElse("-")}
         |Exp : ${result.exp.map(_.value).getOrElse("-")}
         |Single : ${result.single.map(_.value).getOrElse("-")}
         |Double : ${result.double.map(_.value).getOrElse("-")}
         |Triple : ${result.triple.map(_.value).getOrElse("-")}
         |Tetris : ${result.tetris.map(_.value).getOrElse("-")}
         |TSpin : ${result.tSpin.map(_.value).getOrElse("-")}
         |M-TS : ${result.miniTSpin.map(_.value).getOrElse("-")}
         |TS-S : ${result.tSpinSingle.map(_.value).getOrElse("-")}
         |TS-D : ${result.tSpinDouble.map(_.value).getOrElse("-")}
         |TS-T : ${result.tSpinTriple.map(_.value).getOrElse("-")}
         |MaxRen : ${result.maxRen.map(_.value).getOrElse("-")}
         |BtoB : ${result.backToBack.map(_.value).getOrElse("-")}
         |AllClear : ${result.allClear.map(_.value).getOrElse("-")}
       """.stripMargin

    Http("https://api.twitter.com/1.1/statuses/update.json")
      .timeout(10000, 10000)
      .postForm(
        Seq(
          "status"                -> text,
          "in_reply_to_status_id" -> tweetId.value
        )
      )
      .oauth(consumer, token)
      .asString
      .throwError
  }
}

object HttpClient extends HttpClient

trait HttpClient {
  def executeString(request: HttpRequest): HttpResponse[String] =
    request.asString.throwError

  def executeBytes(request: HttpRequest): HttpResponse[Array[Byte]] =
    request.asBytes.throwError
}
