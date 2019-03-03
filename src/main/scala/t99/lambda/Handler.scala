package t99.lambda

import java.util

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.beans.BeanProperty
import scala.collection.JavaConverters

class Handler(private val validAuthToken: String) extends RequestHandler[Request, Response] {

  /**
    * Default constructor for Lambda.
    */
  def this() = this(sys.env("AUTH_TOKEN"))

  def handleRequest(input: Request, context: Context): Response = {
    if (input.authToken != validAuthToken) {
      Response(
        401,
        "Invalid token",
        new util.HashMap(),
        base64Encoded = true
      )
    } else {
      handleInternal(input, context)
    }
  }

  private def handleInternal(input: Request, context: Context): Response = {
    val headers = Map("x-custom-response-header" -> "my custom response header value")
    Response(
      200,
      "Go Serverless v1.0! Your function executed successfully!",
      JavaConverters.mapAsJavaMap[String, Object](headers),
      base64Encoded = true
    )
  }
}

class Request(
    @BeanProperty var authToken: String
) {
  def this() = this("")
}

case class Response(
    @BeanProperty statusCode: Integer,
    @BeanProperty body: String,
    @BeanProperty headers: java.util.Map[String, Object],
    @BeanProperty base64Encoded: Boolean = false
)
