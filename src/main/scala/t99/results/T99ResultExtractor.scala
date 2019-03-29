package t99.results
import t99.rekognition.DetectedTextResults

import scala.util.Try

class T99ResultExtractor {
  def extract(detection: DetectedTextResults): T99Result = {
    val texts = detection.texts.map(_.getDetectedText)
    val resultValues = for {
      extractor <- T99ResultValueExtractors.all
      value     <- extractor.extract(texts).toSeq
    } yield value
    resultValues.foldLeft(T99Result.empty)(_ added _)
  }
}

trait T99ResultValueExtractor {
  def extract(texts: Seq[String]): Option[T99ResultValue]
}

object T99ResultValueExtractors {

  import T99ResultValueType._

  object KOExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(Ko, texts, 2) { case Seq("K.O.", n) => n }
  }

  object ExpExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(Exp, texts, 2) { case Seq("EXP", n) => n }
  }

  object SingleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(Single, texts, 2) { case Seq("Single", n) => n }
  }

  object DoubleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(Double, texts, 2) { case Seq("Double", n) => n }
  }

  object TripleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(Triple, texts, 2) { case Seq("Triple", n) => n }
  }

  object TetrisExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(Tetris, texts, 2) { case Seq("Tetris", n) => n }
  }

  object TSpinExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(TSpin, texts, 2) { case Seq("T-Spin", n) => n }
  }

  object MiniTSpinExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(MiniTSpin, texts, 3) { case Seq("Mini", "T-Spin", n) => n }
  }

  object TSpinSingleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(TSpinSingle, texts, 3) { case Seq("T-Spin", "Single", n) => n }
  }

  object TSpinDoubleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(TSpinDouble, texts, 3) { case Seq("T-Spin", "Double", n) => n }
  }

  object TSpinTripleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(TSpinTriple, texts, 3) { case Seq("T-Spin", "Triple", n) => n }
  }

  object MaxRenExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(MaxRen, texts, 2) { case Seq(ren, n) if ren.endsWith("REN") => n }
  }

  object BackToBackExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(BackToBack, texts, 2) { case Seq("Back-to-Back", n) => n }
  }

  object AllClearExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[T99ResultValue] =
      extractInternal(AllClear, texts, 3) { case Seq("All", "Clear", n) => n }
  }

  private def extractInternal(typ: T99ResultValueType, texts: Seq[String], sliding: Int)(
      pf: PartialFunction[Seq[String], String]
  ): Option[T99ResultValue] =
    texts
      .sliding(sliding)
      .collectFirst(pf)
      .flatMap(toIntCleansing)
      .map(n => T99ResultValue(typ, n))

  private def toIntCleansing(numString: String): Option[Int] =
    Try(numString.replaceAll("O", "0").toInt).toOption

  val all: Seq[T99ResultValueExtractor] = Seq(
    KOExtractor,
    ExpExtractor,
    SingleExtractor,
    DoubleExtractor,
    TripleExtractor,
    TetrisExtractor,
    TSpinExtractor,
    MiniTSpinExtractor,
    TSpinSingleExtractor,
    TSpinDoubleExtractor,
    TSpinTripleExtractor,
    MaxRenExtractor,
    BackToBackExtractor,
    AllClearExtractor
  )
}
