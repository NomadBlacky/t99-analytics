package t99.results
import org.scalatest.{FunSpec, MustMatchers}
import utils.SnakePickle

class T99ResultSpec extends FunSpec with MustMatchers {

  describe("jsonRW") {
    import T99Result.jsonRW

    it("should read a valid JSON") {
      val json =
        """{
          |  "ko": 10,
          |  "exp": 20,
          |  "tetris": 30,
          |  "t_spin_double": 40
          |}
        """.stripMargin.trim

      val expect = T99Result(
        Seq(
          T99ResultValue(T99ResultValueType.Ko, 10),
          T99ResultValue(T99ResultValueType.Exp, 20),
          T99ResultValue(T99ResultValueType.Tetris, 30),
          T99ResultValue(T99ResultValueType.TSpinDouble, 40)
        )
      )

      val actual = SnakePickle.read(json)

      actual mustBe expect
    }

    it("should write a valid JSON") {
      val t99Result = T99Result(
        Seq(
          T99ResultValue(T99ResultValueType.Ko, 10),
          T99ResultValue(T99ResultValueType.Exp, 20),
          T99ResultValue(T99ResultValueType.Tetris, 30),
          T99ResultValue(T99ResultValueType.TSpinDouble, 40)
        )
      )

      val expect =
        """{
          |  "ko": 10,
          |  "exp": 20,
          |  "tetris": 30,
          |  "t_spin_double": 40
          |}
        """.stripMargin.trim

      val actual = SnakePickle.write(t99Result, 2)

      actual mustBe expect
    }
  }
}
