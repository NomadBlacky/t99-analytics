package t99.dynamodb
import java.net.URI

import com.github.j5ik2o.reactive.aws.dynamodb.DynamoDbAsyncClient
import org.scalatest.{AsyncFunSpec, BeforeAndAfterEach, MustMatchers}
import software.amazon.awssdk.services.dynamodb.model.{AttributeDefinition, KeySchemaElement, ProvisionedThroughput}
import software.amazon.awssdk.services.dynamodb.{DynamoDbAsyncClient => JavaDynamoDbAsyncClient}
import t99.results.{T99Result, T99ResultValue, T99ResultValueType}
import t99.twitter.model.TweetId

import scala.concurrent.Await
import scala.concurrent.duration._

class T99DynamoDbClientIntegrationTest extends AsyncFunSpec with BeforeAndAfterEach with MustMatchers {

  val tableName = "T99TEST"

  val underlingClient =
    DynamoDbAsyncClient.apply(
      JavaDynamoDbAsyncClient.builder().endpointOverride(new URI("http://localhost:8000")).build()
    )

  val client = new T99DynamoDbClient(underlingClient, tableName)

  override def beforeEach(): Unit = {
    val future = underlingClient.createTable(
      Seq(AttributeDefinition.builder().attributeName("TweetId").attributeType("S").build()),
      tableName,
      Seq(KeySchemaElement.builder().attributeName("TweetId").keyType("HASH").build()),
      ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build()
    )
    Await.result(future, 5.seconds)
  }

  override def afterEach(): Unit = {
    Await.result(underlingClient.deleteTable(tableName), 5.seconds)
  }

  describe("putResult") {

    it("should put T99Result to a DynamoDB instance") {
      val tweetId = TweetId("abcdefg")
      val result = T99Result(
        Seq(
          T99ResultValue(T99ResultValueType.Exp, 100),
          T99ResultValue(T99ResultValueType.Tetris, 20),
          T99ResultValue(T99ResultValueType.TSpinDouble, 30),
        )
      )

      client.putResult(tweetId, result).map(_ => succeed)
    }
  }

}
