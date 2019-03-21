package t99.results
import utils.SnakePickle

case class T99Result(
    ko: Option[T99ResultValue.KO],
    exp: Option[T99ResultValue.Exp],
    single: Option[T99ResultValue.Single],
    double: Option[T99ResultValue.Double],
    triple: Option[T99ResultValue.Triple],
    tetris: Option[T99ResultValue.Tetris],
    tSpin: Option[T99ResultValue.TSpin],
    miniTSpin: Option[T99ResultValue.MiniTSpin],
    tSpinSingle: Option[T99ResultValue.TSpinSingle],
    tSpinDouble: Option[T99ResultValue.TSpinDouble],
    tSpinTriple: Option[T99ResultValue.TSpinTriple],
    maxRen: Option[T99ResultValue.MaxRen],
    backToBack: Option[T99ResultValue.BackToBack],
    allClear: Option[T99ResultValue.AllClear]
) {
  def added(value: T99ResultValue): T99Result = value match {
    case v: T99ResultValue.KO          => copy(ko = Some(v))
    case v: T99ResultValue.Exp         => copy(exp = Some(v))
    case v: T99ResultValue.Single      => copy(single = Some(v))
    case v: T99ResultValue.Double      => copy(double = Some(v))
    case v: T99ResultValue.Triple      => copy(triple = Some(v))
    case v: T99ResultValue.Tetris      => copy(tetris = Some(v))
    case v: T99ResultValue.TSpin       => copy(tSpin = Some(v))
    case v: T99ResultValue.MiniTSpin   => copy(miniTSpin = Some(v))
    case v: T99ResultValue.TSpinSingle => copy(tSpinSingle = Some(v))
    case v: T99ResultValue.TSpinDouble => copy(tSpinDouble = Some(v))
    case v: T99ResultValue.TSpinTriple => copy(tSpinTriple = Some(v))
    case v: T99ResultValue.MaxRen      => copy(maxRen = Some(v))
    case v: T99ResultValue.BackToBack  => copy(backToBack = Some(v))
    case v: T99ResultValue.AllClear    => copy(allClear = Some(v))
  }

  def merge(other: T99Result): T99Result = {
    def mergeValue[V <: T99ResultValue](self: Option[V], that: Option[V]): Option[V] =
      (self, that) match {
        case (Some(v), _) => Some(v)
        case (_, Some(v)) => Some(v)
        case _            => None
      }

    T99Result(
      ko = mergeValue(this.ko, other.ko),
      exp = mergeValue(this.exp, other.exp),
      single = mergeValue(this.single, other.single),
      double = mergeValue(this.double, other.double),
      triple = mergeValue(this.triple, other.triple),
      tetris = mergeValue(this.tetris, other.tetris),
      tSpin = mergeValue(this.tSpin, other.tSpin),
      miniTSpin = mergeValue(this.miniTSpin, other.miniTSpin),
      tSpinSingle = mergeValue(this.tSpinSingle, other.tSpinSingle),
      tSpinDouble = mergeValue(this.tSpinDouble, other.tSpinDouble),
      tSpinTriple = mergeValue(this.tSpinTriple, other.tSpinTriple),
      maxRen = mergeValue(this.maxRen, other.maxRen),
      backToBack = mergeValue(this.backToBack, other.backToBack),
      allClear = mergeValue(this.allClear, other.allClear)
    )
  }
}

object T99Result {
  val empty = T99Result(None, None, None, None, None, None, None, None, None, None, None, None, None, None)

  implicit val t99ResultRW: SnakePickle.ReadWriter[T99Result] = SnakePickle.macroRW[T99Result]
}
