import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
//For Notes
import controllers.NoteController
import db.{MongoManager, NoteRepository}
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global

object Server extends HttpServer {

  override val disableAdminHttpServer = true
  override def configureHttp(router: HttpRouter): Unit = {
     val config = ConfigFactory.load()
    val mongoUri = config.getString("mongo.uri")
    val dbName   = config.getString("mongo.db")

    val mongoClient = new MongoManager(mongoUri)
    val noteRepo    = new NoteRepository(mongoClient, dbName)
    

    router.add(new NoteController(noteRepo))

    router.add[HelloController]
  }
}
