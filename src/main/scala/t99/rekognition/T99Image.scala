package t99.rekognition
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

import javax.imageio.ImageIO

case class T99Image(bufferedImage: BufferedImage) {
  import T99Image._

  def croppedImage: ByteBuffer = {
    val bi = bufferedImage.getSubimage(StartX, StartY, Width, Height)

    val out = new ByteArrayOutputStream
    ImageIO.write(bi, imageType, out)
    ByteBuffer.wrap(out.toByteArray)
  }
}

object T99Image {
  final val imageType = "jpg"

  final val StartX = 400
  final val StartY = 190
  final val Width  = 430
  final val Height = 470
}
