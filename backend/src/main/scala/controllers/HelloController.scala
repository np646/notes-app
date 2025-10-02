import com.twitter.finatra.http.Controller
import com.twitter.finagle.http.Request

case class HelloRequest(name: String)
case class HelloResponse(message: String)

class HelloController extends Controller {
  options("/api/hello") { request: Request =>
    response
      .ok("")
      .header("Access-Control-Allow-Origin", "*")
      .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
      .header(
        "Access-Control-Allow-Headers",
        "Content-Type, Authorization, X-Requested-With"
      )
      .header("Access-Control-Max-Age", "86400")
  }

  get("/api/hello") { request: Request =>
    val resp = HelloResponse(
      message = "Hello from Finatra!"
    )
    response
      .ok(resp)
      .header("Access-Control-Allow-Origin", "*")
  }

  post("/api/hello") { request: HelloRequest =>
    val resp = HelloResponse(
      message =
        s"Hello, ${request.name}! This is a message from Finatra backend."
    )
    response
      .ok(resp)
      .header("Access-Control-Allow-Origin", "*")
  }
}
