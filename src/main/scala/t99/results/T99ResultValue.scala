package t99.results
import utils.SnakePickle

sealed trait T99ResultValue {
  val value: Int
}

object T99ResultValue {
  @upickle.implicits.key("ko")
  case class KO(value: Int) extends T99ResultValue
  object KO {
    implicit val rw: SnakePickle.ReadWriter[KO] = SnakePickle.readwriter[Int].bimap(_.value, KO(_))
  }

  case class Exp(value: Int) extends T99ResultValue
  object Exp {
    implicit val rw: SnakePickle.ReadWriter[Exp] = SnakePickle.readwriter[Int].bimap(_.value, Exp(_))
  }

  case class Single(value: Int) extends T99ResultValue
  object Single {
    implicit val rw: SnakePickle.ReadWriter[Single] = SnakePickle.readwriter[Int].bimap(_.value, Single(_))
  }

  case class Double(value: Int) extends T99ResultValue
  object Double {
    implicit val rw: SnakePickle.ReadWriter[Double] = SnakePickle.readwriter[Int].bimap(_.value, Double(_))
  }

  case class Triple(value: Int) extends T99ResultValue
  object Triple {
    implicit val rw: SnakePickle.ReadWriter[Triple] = SnakePickle.readwriter[Int].bimap(_.value, Triple(_))
  }

  case class Tetris(value: Int) extends T99ResultValue
  object Tetris {
    implicit val rw: SnakePickle.ReadWriter[Tetris] = SnakePickle.readwriter[Int].bimap(_.value, Tetris(_))
  }

  case class TSpin(value: Int) extends T99ResultValue
  object TSpin {
    implicit val rw: SnakePickle.ReadWriter[TSpin] = SnakePickle.readwriter[Int].bimap(_.value, TSpin(_))
  }

  case class MiniTSpin(value: Int) extends T99ResultValue
  object MiniTSpin {
    implicit val rw: SnakePickle.ReadWriter[MiniTSpin] = SnakePickle.readwriter[Int].bimap(_.value, MiniTSpin(_))
  }

  case class TSpinSingle(value: Int) extends T99ResultValue
  object TSpinSingle {
    implicit val rw: SnakePickle.ReadWriter[TSpinSingle] = SnakePickle.readwriter[Int].bimap(_.value, TSpinSingle(_))
  }

  case class TSpinDouble(value: Int) extends T99ResultValue
  object TSpinDouble {
    implicit val rw: SnakePickle.ReadWriter[TSpinDouble] = SnakePickle.readwriter[Int].bimap(_.value, TSpinDouble(_))
  }

  case class TSpinTriple(value: Int) extends T99ResultValue
  object TSpinTriple {
    implicit val rw: SnakePickle.ReadWriter[TSpinTriple] = SnakePickle.readwriter[Int].bimap(_.value, TSpinTriple(_))
  }

  case class MaxRen(value: Int) extends T99ResultValue
  object MaxRen {
    implicit val rw: SnakePickle.ReadWriter[MaxRen] = SnakePickle.readwriter[Int].bimap(_.value, MaxRen(_))
  }

  case class BackToBack(value: Int) extends T99ResultValue
  object BackToBack {
    implicit val rw: SnakePickle.ReadWriter[BackToBack] = SnakePickle.readwriter[Int].bimap(_.value, BackToBack(_))
  }

  case class AllClear(value: Int) extends T99ResultValue
  object AllClear {
    implicit val rw: SnakePickle.ReadWriter[AllClear] = SnakePickle.readwriter[Int].bimap(_.value, AllClear(_))
  }

  implicit val resultValueRW: SnakePickle.ReadWriter[T99ResultValue] = SnakePickle.macroRW
}
