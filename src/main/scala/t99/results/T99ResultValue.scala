package t99.results

sealed trait T99ResultValue

object T99ResultValue {
  case class KO(value: Int)          extends T99ResultValue
  case class Exp(value: Int)         extends T99ResultValue
  case class Single(value: Int)      extends T99ResultValue
  case class Double(value: Int)      extends T99ResultValue
  case class Triple(value: Int)      extends T99ResultValue
  case class Tetris(value: Int)      extends T99ResultValue
  case class TSpin(value: Int)       extends T99ResultValue
  case class MiniTSpin(value: Int)   extends T99ResultValue
  case class TSpinSingle(value: Int) extends T99ResultValue
  case class TSpinDouble(value: Int) extends T99ResultValue
  case class TSpinTriple(value: Int) extends T99ResultValue
  case class MaxRen(value: Int)      extends T99ResultValue
  case class BackToBack(value: Int)  extends T99ResultValue
  case class AllClear(value: Int)    extends T99ResultValue
}
