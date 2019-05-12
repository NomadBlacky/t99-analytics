package t99.twitter
import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder
import javax.imageio.ImageIO
import org.apache.http.client.methods.{HttpGet, HttpUriRequest}
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.{HttpException, HttpResponse}
import t99.rekognition.T99Image
import t99.twitter.model.{Tweet, TweetId}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class TwitterClient(oauth2Token: String, http: HttpClient = HttpClient) {
  def getTweet(id: TweetId)(implicit ec: ExecutionContext): Future[Tweet] =
    Future {
      val request = {
        val uri = new URIBuilder("https://api.twitter.com/1.1/statuses/show.json")
          .addParameter("id", id.value.toString)
          .build()
        val get = new HttpGet(uri)
        get.setHeader("Authorization", s"Bearer $oauth2Token")
        get
      }
      val response = http.execute(request)

      val body = Source.fromInputStream(response.getEntity.getContent, "UTF-8").mkString
      upickle.default.read[Tweet](body)
    }

  def getImages(tweet: Tweet)(implicit ec: ExecutionContext): Future[Seq[T99Image]] = {
    Future.traverse(tweet.photos) { media =>
      val request = new HttpGet(media.mediaUrlHttps)
      Future {
        val response = http.execute(request)
        val image    = ImageIO.read(response.getEntity.getContent)
        T99Image(image)
      }
    }
  }
}

object HttpClient extends HttpClient {
  val client: CloseableHttpClient = HttpClientBuilder.create().build()

  @throws[HttpException]("if HTTP response code is not 200")
  def execute(request: HttpUriRequest): HttpResponse = {
    val response   = client.execute(request)
    val statusCode = response.getStatusLine.getStatusCode
    if (statusCode == 200) {
      response
    } else {
      throw new HttpException(s"HTTP Request Failed. Status $statusCode")
    }
  }
}

trait HttpClient {
  def execute(request: HttpUriRequest): HttpResponse
}
