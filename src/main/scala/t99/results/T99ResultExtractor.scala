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

  import T99ResultValue._

  object KOExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[KO] =
      extractInternal(texts, 2) { case Seq("K.O.", n) => n }.map(KO.apply)
  }

  object ExpExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[Exp] =
      extractInternal(texts, 2) { case Seq("EXP", n) => n }.map(Exp.apply)
  }

  object SingleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[Single] =
      extractInternal(texts, 2) { case Seq("Single", n) => n }.map(Single.apply)
  }

  object DoubleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[Double] =
      extractInternal(texts, 2) { case Seq("Double", n) => n }.map(Double.apply)
  }

  object TripleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[Triple] =
      extractInternal(texts, 2) { case Seq("Triple", n) => n }.map(Triple.apply)
  }

  object TetrisExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[Tetris] =
      extractInternal(texts, 2) { case Seq("Tetris", n) => n }.map(Tetris.apply)
  }

  object TSpinExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[TSpin] =
      extractInternal(texts, 2) { case Seq("T-Spin", n) => n }.map(TSpin.apply)
  }

  object MiniTSpinExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[MiniTSpin] =
      extractInternal(texts, 3) { case Seq("Mini", "T-Spin", n) => n }.map(MiniTSpin.apply)
  }

  object TSpinSingleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[TSpinSingle] =
      extractInternal(texts, 3) { case Seq("T-Spin", "Single", n) => n }.map(TSpinSingle.apply)
  }

  object TSpinDoubleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[TSpinDouble] =
      extractInternal(texts, 3) { case Seq("T-Spin", "Double", n) => n }.map(TSpinDouble.apply)
  }

  object TSpinTripleExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[TSpinTriple] =
      extractInternal(texts, 3) { case Seq("T-Spin", "Triple", n) => n }.map(TSpinTriple.apply)
  }

  object MaxRenExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[MaxRen] =
      extractInternal(texts, 2) { case Seq(ren, n) if ren.endsWith("REN") => n }.map(MaxRen.apply)
  }

  object BackToBackExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[BackToBack] =
      extractInternal(texts, 2) { case Seq("Back-to-Back", n) => n }.map(BackToBack.apply)
  }

  object AllClearExtractor extends T99ResultValueExtractor {
    def extract(texts: Seq[String]): Option[AllClear] =
      extractInternal(texts, 3) { case Seq("All", "Clear", n) => n }.map(AllClear.apply)
  }

  private def extractInternal(texts: Seq[String], sliding: Int)(pf: PartialFunction[Seq[String], String]): Option[Int] =
    texts
      .sliding(sliding)
      .collectFirst(pf)
      .flatMap(toIntCleansing)

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
