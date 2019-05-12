package t99.twitter.model
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

import ujson.{Value => Json}
import upickle.default._

import scala.util.Try

case class Tweet(id: TweetId, medias: Seq[TweetMedia], createdAt: Instant) {
  val photos: Seq[TweetMedia] = medias.filter(_.typ == TweetMediaType.Photo)
}

case class TweetId(value: String)

case class TweetMedia(typ: TweetMediaType, mediaUrlHttps: String)

sealed abstract class TweetMediaType(val value: String)
object TweetMediaType {
  case object Photo       extends TweetMediaType("photo")
  case object Video       extends TweetMediaType("video")
  case object AnimatedGif extends TweetMediaType("animated_gif")

  val all: Set[TweetMediaType] = Set(Photo, Video, AnimatedGif)

  @throws[NoSuchElementException]("if not found")
  def fromName(name: String): TweetMediaType = all.find(_.value == name).get
}

object Tweet {
  val datetimeFormatter: SimpleDateFormat =
    new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.ENGLISH)

  implicit val tweetReader: Reader[Tweet] =
    reader[Json].map { json =>
      val extendedEntities =
        Try(json("extended_entities")("media").arr).getOrElse(Seq.empty)

      Tweet(
        TweetId(json("id_str").str),
        extendedEntities.map(read[TweetMedia](_)),
        Instant.from(datetimeFormatter.parse(json("created_at").str).toInstant)
      )
    }

  implicit val tweetMediaReader: Reader[TweetMedia] =
    reader[Json].map { json =>
      val typ = TweetMediaType.fromName(json("type").str)
      val url = json("media_url_https").str
      TweetMedia(typ, url)
    }
}
