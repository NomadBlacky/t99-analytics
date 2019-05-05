package t99.rekognition

import com.github.j5ik2o.reactive.aws.rekognition.RekognitionAsyncClient
import com.github.j5ik2o.reactive.aws.rekognition.implicits._
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.rekognition.model.{DetectTextRequest, Image, TextDetection}

import scala.concurrent.{ExecutionContext, Future}

class RekognitionClient(rekognition: RekognitionAsyncClient) {
  def detectTexts(image: T99Image)(implicit ec: ExecutionContext): Future[DetectedTextResults] = {
    val img     = Image.builder().bytes(SdkBytes.fromByteBuffer(image.croppedImage)).build()
    val request = DetectTextRequest.builder().image(img).build()

    for {
      result     <- rekognition.detectText(request)
      detections = result.textDetectionsAsScala.getOrElse(Seq.empty)
    } yield DetectedTextResults(detections)
  }
}

case class DetectedTextResults(texts: Seq[TextDetection])
