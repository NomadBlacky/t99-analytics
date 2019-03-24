package t99.results
import utils.SnakePickle

case class T99ResultValue(
    @upickle.implicits.key("type") resultType: T99ResultValueType,
    value: Int
)

object T99ResultValue {
  implicit val jsonRW: SnakePickle.ReadWriter[T99ResultValue] = SnakePickle.macroRW
}
