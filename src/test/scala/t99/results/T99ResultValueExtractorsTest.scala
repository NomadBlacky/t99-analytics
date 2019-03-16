package t99.results
import org.scalatest.{FunSpec, MustMatchers}
import t99.results.T99ResultValue._
import t99.results.T99ResultValueExtractors._

class T99ResultValueExtractorsTest extends FunSpec with MustMatchers {

  describe("KOExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "K.O.", "20", "bbb")
      val result = KOExtractor.extract(texts)
      result mustBe Some(KO(20))
    }
  }

  describe("ExpExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "EXP", "1000", "bbb")
      val result = ExpExtractor.extract(texts)
      result mustBe Some(Exp(1000))
    }
  }

  describe("SingleExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "Single", "30", "bbb")
      val result = SingleExtractor.extract(texts)
      result mustBe Some(Single(30))
    }
  }

  describe("DoubleExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "Double", "25", "bbb")
      val result = DoubleExtractor.extract(texts)
      result mustBe Some(Double(25))
    }
  }

  describe("TripleExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "Triple", "7", "bbb")
      val result = TripleExtractor.extract(texts)
      result mustBe Some(Triple(7))
    }
  }

  describe("TetrisExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "Tetris", "22", "bbb")
      val result = TetrisExtractor.extract(texts)
      result mustBe Some(Tetris(22))
    }
  }

  describe("TSpinExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "T-Spin", "2", "bbb")
      val result = TSpinExtractor.extract(texts)
      result mustBe Some(TSpin(2))
    }
  }

  describe("MiniTSpinExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "Mini", "T-Spin", "3", "bbb")
      val result = MiniTSpinExtractor.extract(texts)
      result mustBe Some(MiniTSpin(3))
    }
  }

  describe("TSpinSingleExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "T-Spin", "Single", "0", "bbb")
      val result = TSpinSingleExtractor.extract(texts)
      result mustBe Some(TSpinSingle(0))
    }
  }

  describe("TSpinDoubleExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "T-Spin", "Double", "15", "bbb")
      val result = TSpinDoubleExtractor.extract(texts)
      result mustBe Some(TSpinDouble(15))
    }
  }

  describe("TSpinTripleExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "T-Spin", "Triple", "2", "bbb")
      val result = TSpinTripleExtractor.extract(texts)
      result mustBe Some(TSpinTriple(2))
    }
  }

  describe("MaxRenExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "fdajREN", "8", "bbb")
      val result = MaxRenExtractor.extract(texts)
      result mustBe Some(MaxRen(8))
    }
  }

  describe("BackToBackExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "Back-to-Back", "20", "bbb")
      val result = BackToBackExtractor.extract(texts)
      result mustBe Some(BackToBack(20))
    }
  }

  describe("AllClearExtractor") {
    it("extract") {
      val texts  = Seq("aaa", "All", "Clear", "1", "bbb")
      val result = AllClearExtractor.extract(texts)
      result mustBe Some(AllClear(1))
    }
  }
}
