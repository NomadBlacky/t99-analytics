package t99.results
import utils.SnakePickle

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
}

object T99Result {
  val empty = T99Result(Seq.empty)

  implicit val jsonRW: SnakePickle.ReadWriter[T99Result] = SnakePickle.macroRW
}
