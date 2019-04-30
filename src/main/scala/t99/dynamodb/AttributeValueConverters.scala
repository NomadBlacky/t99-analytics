package t99.dynamodb
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object AttributeValueConverters {
  implicit def asAttributeValue[A](a: A)(implicit cv: AttributeValueConverter[A]): AsAttributeValue =
    new AsAttributeValue {
      def asAttributeValue: AttributeValue = cv.asAttributeValue(a)
    }

  implicit val stringAsAttributeValue: AttributeValueConverter[String] = AttributeValue.builder().s(_).build()

  implicit val intAsAttributeValue: AttributeValueConverter[Int] = i => AttributeValue.builder().n(i.toString).build()

  implicit def mapAsAttributeValue[A](
      implicit cv: AttributeValueConverter[A]
  ): AttributeValueConverter[Map[String, A]] =
    (map: Map[String, A]) => AttributeValue.builder().m(map.mapValues(cv.asAttributeValue).asJava).build()
}

trait AttributeValueConverter[A] {
  def asAttributeValue(a: A): AttributeValue
}

trait AsAttributeValue {
  def asAttributeValue: AttributeValue
}
