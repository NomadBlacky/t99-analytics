package t99.twitter.model
import ujson.{Value => Json}
import upickle.default._

import scala.util.Try

case class Tweet(id: TweetId, medias: Seq[TweetMedia])

case class TweetId(value: String)

case class TweetMedia(mediaUrlHttps: String)

object Tweet {
  implicit val tweetReader: Reader[Tweet] =
    reader[Json].map { json =>
      val extendedEntities =
        Try(json("extended_entities")("media").arr).getOrElse(Seq.empty)

      Tweet(
        TweetId(json("id_str").str),
        extendedEntities.map(read[TweetMedia](_))
      )
    }

  implicit val tweetMediaReader: Reader[TweetMedia] =
    reader[Json].map(json => TweetMedia(json("media_url_https").str))
}
