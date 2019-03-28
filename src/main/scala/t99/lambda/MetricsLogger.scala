package t99.lambda
import java.time.Instant

object MetricsLogger {
  val namespace = "t99"

  def send[N: Numeric](name: String, value: N, unit: String, timestamp: Instant = Instant.now()): Unit = {
    println(s"MONITORING|${timestamp.getEpochSecond}|$value|$unit|$name|$namespace")
  }
}
