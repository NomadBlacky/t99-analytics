package t99.rekognition
import com.amazonaws.services.rekognition.AmazonRekognition
import com.amazonaws.services.rekognition.model.{DetectTextRequest, Image, TextDetection}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class RekognitionClient(rekognition: AmazonRekognition) {
  def detectTexts(image: T99Image)(implicit ec: ExecutionContext): Future[DetectedTextResults] = Future {
    val request = new DetectTextRequest().withImage(new Image().withBytes(image.croppedImage))
    val result  = rekognition.detectText(request)

    val detections = result.getTextDetections.asScala.toList
    DetectedTextResults(detections)
  }
}

case class DetectedTextResults(texts: Seq[TextDetection])
