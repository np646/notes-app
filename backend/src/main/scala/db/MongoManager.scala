package db

import reactivemongo.api.{AsyncDriver, MongoConnection, DB}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

class MongoManager(mongoUri: String)(implicit ec: ExecutionContext) {
  private val driver = AsyncDriver()

  private val connectionF: Future[MongoConnection] =
    MongoConnection.fromString(mongoUri).flatMap { parsedUri =>
      driver.connect(parsedUri)
    }

  // To make sure connection is established at startup
  def initialize(): Unit = {
    try {
      Await.result(connectionF, 10.seconds)
      println(s"MongoDB connection established to: $mongoUri")
    } catch {
      case ex: Exception =>
        println(s"Failed to connect to MongoDB: ${ex.getMessage}")
        ex.printStackTrace()
    }
  }

  def database(dbName: String): Future[DB] = {
    connectionF.flatMap(_.database(dbName))
  }

  def close(): Future[Unit] = {
    connectionF.flatMap { conn =>
      driver.close()
    }
  }
}
