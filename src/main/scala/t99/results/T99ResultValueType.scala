package t99.results
import utils.SnakePickle

sealed trait T99ResultValueType {
  val name: String = toString.split("(?=[A-Z])", -1).map(_.toLowerCase).mkString("_")
}

object T99ResultValueType {
  case object Ko          extends T99ResultValueType
  case object Exp         extends T99ResultValueType
  case object Single      extends T99ResultValueType
  case object Double      extends T99ResultValueType
  case object Triple      extends T99ResultValueType
  case object Tetris      extends T99ResultValueType
  case object TSpin       extends T99ResultValueType
  case object MiniTSpin   extends T99ResultValueType
  case object TSpinSingle extends T99ResultValueType
  case object TSpinDouble extends T99ResultValueType
  case object TSpinTriple extends T99ResultValueType
  case object MaxRen      extends T99ResultValueType
  case object BackToBack  extends T99ResultValueType
  case object AllClear    extends T99ResultValueType

  val allTypes: Set[T99ResultValueType] = Set(
    Ko,
    Exp,
    Single,
    Double,
    Triple,
    Tetris,
    TSpin,
    MiniTSpin,
    TSpinSingle,
    TSpinDouble,
    TSpinTriple,
    MaxRen,
    BackToBack,
    AllClear
  )

  val mapping: Map[String, T99ResultValueType] = allTypes.map(vt => vt.name -> vt).toMap

  def fromName(key: String): Option[T99ResultValueType] = mapping.get(key)

  implicit val jsonRW: SnakePickle.ReadWriter[T99ResultValueType] =
    SnakePickle.readwriter[String].bimap[T99ResultValueType](_.name, mapping.apply)
}
