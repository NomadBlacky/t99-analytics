package t99.lambda
import java.time.Instant

import org.scalatest.{FunSpec, MustMatchers}

class MetricsLoggerSpec extends FunSpec with MustMatchers {

  describe("buildDogStatsDFormat") {
    it("should build DogStatsD format string") {
      val mLogger   = MetricsLogger.apply("prefix", "test", TestContext())
      val timestamp = Instant.now()
      val actual    = mLogger.buildDogStatsDFormat("name", 100, "gauge", timestamp)
      val expect =
        s"MONITORING|${timestamp.getEpochSecond}|100|gauge|prefix_name|#t99-analytics,function_name:functionName,function_version:functionVersion,function_arn:invokedFunctionArn,env:test"

      actual mustBe expect
    }
  }
}
