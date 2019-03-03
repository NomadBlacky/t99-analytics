package t99.lambda

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.beans.BeanProperty
import scala.collection.JavaConverters

class Handler extends RequestHandler[Request, Response] {

  def handleRequest(input: Request, context: Context): Response = {
    val headers = Map("x-custom-response-header" -> "my custom response header value")
    Response(
      200,
      "Go Serverless v1.0! Your function executed successfully!",
      JavaConverters.mapAsJavaMap[String, Object](headers),
      base64Encoded = true
    )
  }
}

class Request(@BeanProperty var key1: String, @BeanProperty var key2: String, @BeanProperty var key3: String) {
  def this() = this("", "", "")
}

case class Response(
    @BeanProperty statusCode: Integer,
    @BeanProperty body: String,
    @BeanProperty headers: java.util.Map[String, Object],
    @BeanProperty base64Encoded: Boolean = false
)
