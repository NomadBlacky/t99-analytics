package t99.results
import java.time.Instant

import t99.lambda.MetricsLogger
import ujson.{Value => Json}
import utils.SnakePickle

import scala.collection.mutable

case class T99Result(values: Seq[T99ResultValue]) {
  require(
    !values.groupBy(_.resultType).exists(_._2.length >= 2),
    s"Duplicate `T99ResultValueType`s: $values"
  )

  def added(value: T99ResultValue): T99Result =
    if (values.exists(_.resultType == value.resultType)) this
    else T99Result(values :+ value)

  def merge(other: T99Result): T99Result = {
    val values = (this.values ++ other.values).groupBy(_.resultType).map(_._2.head)
    T99Result(values.toSeq)
  }

  def sendMetrics(createdAt: Instant): Unit = T99ResultValueType.allTypes.foreach { typ =>
    values
      .find(_.resultType == typ)
      .fold(MetricsLogger.send(s"${typ.name}_missing", 1, "count", createdAt))(
        rv => MetricsLogger.send(s"${typ.name}_value", rv.value, "gauge", createdAt)
      )
  }
}

object T99Result {
  val empty = T99Result(Seq.empty)

  implicit val jsonRW: SnakePickle.ReadWriter[T99Result] = SnakePickle
    .readwriter[Json]
    .bimap[T99Result](
      result => {
        val keyValue: Map[String, Json] =
          result.values.map(rv => rv.resultType.name -> ujson.Num(rv.value)).toMap
        ujson.Obj(mutable.LinkedHashMap(keyValue.toSeq: _*))
      },
      json => {
        val values = json.obj.map { case (k, v) => T99ResultValue(T99ResultValueType.fromName(k).get, v.num.toInt) }
        T99Result(values.toSeq)
      }
    )
}
