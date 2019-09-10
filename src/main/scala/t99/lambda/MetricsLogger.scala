package t99.lambda
import java.time.Instant

import com.amazonaws.services.lambda.runtime.Context

class MetricsLogger private (namePrefix: String, tags: Seq[String]) {
  private[this] val prefix = if (namePrefix.isEmpty) "" else s"${namePrefix}_"

  private[lambda] def buildDogStatsDFormat[N: Numeric](
      name: String,
      value: N,
      unit: String,
      timestamp: Instant
  ): String =
    s"MONITORING|${timestamp.getEpochSecond}|$value|$unit|$prefix$name|#${tags.mkString(",")}"

  def send[N: Numeric](name: String, value: N, unit: String, timestamp: Instant = Instant.now()): Unit =
    println(buildDogStatsDFormat(name, value, unit, timestamp))
}

object MetricsLogger {
  def apply(namePrefix: String, env: String, context: Context): MetricsLogger = {
    new MetricsLogger(
      namePrefix,
      Seq(
        "t99-analytics",
        s"function_name:${context.getFunctionName}",
        s"function_version:${context.getFunctionVersion}",
        s"function_arn:${context.getInvokedFunctionArn}",
        s"env:$env"
      )
    )
  }
}
