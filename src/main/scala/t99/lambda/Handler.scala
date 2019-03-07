package t99.lambda

import java.util

import com.amazonaws.services.lambda.runtime.events.{APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent}
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.collection.JavaConverters

class Handler(private val validAuthToken: String) extends RequestHandler[Request, Response] {

  /**
    * Default constructor for Lambda.
    */
  def this() = this(sys.env("AUTH_TOKEN"))

  def handleRequest(input: Request, context: Context): Response = {
    context.getLogger.log(input.toString)

    val bodyJson  = ujson.read(input.getBody)
    val authToken = bodyJson("auth_token").str

    if (authToken != validAuthToken) {
      Response(
        401,
        "Invalid token",
        new util.HashMap()
      )
    } else {
      handleInternal(bodyJson, context)
    }
  }

  private def handleInternal(bodyJson: ujson.Value, context: Context): Response = {
    val headers = Map("x-custom-response-header" -> "my custom response header value")
    Response(
      200,
      "Go Serverless v1.0! Your function executed successfully!",
      JavaConverters.mapAsJavaMap(headers),
    )
  }
}

case class Request(body: String) extends APIGatewayProxyRequestEvent {
  def this() = this("")
  setBody(body)
}

case class Response(
    statusCode: Integer,
    body: String,
    headers: java.util.Map[String, String],
) extends APIGatewayProxyResponseEvent {
  setStatusCode(statusCode)
  setBody(body)
  setHeaders(headers)
}
