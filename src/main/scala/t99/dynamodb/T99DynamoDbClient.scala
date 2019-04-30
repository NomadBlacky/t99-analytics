package t99.dynamodb
import com.github.j5ik2o.reactive.aws.dynamodb._
import com.github.j5ik2o.reactive.aws.dynamodb.implicits._
import software.amazon.awssdk.services.dynamodb.model.{AttributeValue, PutItemRequest, PutItemResponse}
import t99.results.T99Result
import AttributeValueConverters._
import t99.twitter.model.TweetId

import scala.concurrent.Future

class T99DynamoDbClient(client: DynamoDbAsyncClient, tableName: String) {

  def putResult(tweetId: TweetId, t99Result: T99Result): Future[PutItemResponse] = {
    val putItemRequest = PutItemRequest
      .builder()
      .tableName(tableName)
      .itemAsScala(
        Map[String, AttributeValue](
          "TweetId" -> tweetId.value.asAttributeValue,
          "Values"  -> t99Result.toMap.asAttributeValue
        )
      )
      .build()

    client.putItem(putItemRequest)
  }

}
