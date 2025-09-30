package db
import reactivemongo.api.{AsyncDriver, MongoConnection, DB}
import scala.concurrent.{ExecutionContext, Future}

class MongoManager(mongoUri: String)(implicit ec: ExecutionContext) {
  private val driver = AsyncDriver()

  private val connectionF: Future[MongoConnection] =
    for {
      parsedUri   <- MongoConnection.fromString(mongoUri)
      connection  <- driver.connect(parsedUri, None)
    } yield connection

  def database(dbName: String): Future[DB] =
    connectionF.flatMap(_.database(dbName))
}
