package t99.lambda
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

case class Response(
    statusCode: Integer,
    body: String,
    headers: java.util.Map[String, String],
) extends APIGatewayProxyResponseEvent {
  setStatusCode(statusCode)
  setBody(body)
  setHeaders(headers)
}
