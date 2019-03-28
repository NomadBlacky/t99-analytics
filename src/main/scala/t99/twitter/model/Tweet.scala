package t99.twitter.model
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

import ujson.{Value => Json}
import upickle.default._
import utils.SnakePickle

import scala.util.Try

case class Tweet(id: TweetId, medias: Seq[TweetMedia], createdAt: Instant)

case class TweetId(value: String)

case class TweetMedia(`type`: String, mediaUrlHttps: String)

object Tweet {
  val datetimeFormatter: SimpleDateFormat =
    new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.ENGLISH)

  implicit val tweetReader: Reader[Tweet] =
    reader[Json].map { json =>
      val extendedEntities =
        Try(json("extended_entities")("media").arr).getOrElse(Seq.empty)

      Tweet(
        TweetId(json("id_str").str),
        extendedEntities.map(SnakePickle.read[TweetMedia](_)),
        Instant.from(datetimeFormatter.parse(json("created_at").str).toInstant)
      )
    }

  implicit val tweetMediaReader: SnakePickle.Reader[TweetMedia] =
    SnakePickle.macroR[TweetMedia]
}
