package t99.lambda
import java.time.Instant

import com.amazonaws.services.lambda.runtime.Context

class MetricsLogger private (_namePrefix: String, tags: Seq[String]) {
  private[this] val prefix = if (_namePrefix.isEmpty) "" else s"${_namePrefix}_"

  def send[N: Numeric](name: String, value: N, unit: String, timestamp: Instant = Instant.now()): Unit = {
    println(s"MONITORING|${timestamp.getEpochSecond}|$prefix$value|$unit|$name|#${tags.mkString(",")}")
  }
}

object MetricsLogger {
  def apply(_namePrefix: String, env: String, context: Context): MetricsLogger = {
    new MetricsLogger(
      _namePrefix,
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
