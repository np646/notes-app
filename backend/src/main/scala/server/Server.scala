import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.filters.{LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.{Controller, HttpServer}

// For Notes
import controllers.NoteController
import db.{MongoManager, NoteRepository}
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global

object Server extends HttpServer {
  override val disableAdminHttpServer = true

  private lazy val config = ConfigFactory.load()
  private lazy val mongoUri = config.getString("mongo.uri")
  private lazy val dbName = config.getString("mongo.db")
  private lazy val mongoClient = new MongoManager(mongoUri)
  private lazy val noteRepo = new NoteRepository(mongoClient, dbName)

  override def postWarmup(): Unit = {
    super.postWarmup()
    // Initialize MongoDB connection
    mongoClient.initialize()
    println(s"Server ready - MongoDB: $mongoUri, Database: $dbName")
  }

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .add(new NoteController(noteRepo))
      .add[HelloController]
  }

  onExit {
    mongoClient.close()
  }
}
