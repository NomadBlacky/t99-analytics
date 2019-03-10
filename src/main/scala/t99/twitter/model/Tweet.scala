package t99.twitter.model
import ujson.{Value => Json}
import upickle.default._
import utils.SnakePickle

import scala.util.Try

case class Tweet(id: TweetId, medias: Seq[TweetMedia])

case class TweetId(value: String)

case class TweetMedia(`type`: String, mediaUrlHttps: String)

object Tweet {
  implicit val tweetReader: Reader[Tweet] =
    reader[Json].map { json =>
      val extendedEntities =
        Try(json("extended_entities")("media").arr).getOrElse(Seq.empty)

      Tweet(
        TweetId(json("id_str").str),
        extendedEntities.map(SnakePickle.read[TweetMedia](_))
      )
    }

  implicit val tweetMediaReader: SnakePickle.Reader[TweetMedia] =
    SnakePickle.macroR[TweetMedia]
}
