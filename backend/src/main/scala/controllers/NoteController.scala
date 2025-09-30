package controllers
import com.twitter.finatra.http.Controller
import db.NoteRepository
import scala.concurrent.ExecutionContext
import com.twitter.finagle.http.Request
import model.Note

case class CreateNoteRequest(title: String, content: String)

class NoteController(noteRepo: NoteRepository)(implicit ec: ExecutionContext)
    extends Controller {

  options("/notes") { request: Request =>
    response.ok
      .header("Access-Control-Allow-Origin", "*")
      .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
  }
  get("/notes") { request: Request =>
    async {
      noteRepo.findAll().map { notes =>
        response.ok
          .json(notes)
          .header("Access-Control-Allow-Origin", "*")
      }
    }
  }

  case class CreateNoteRequest(title: String, content: String)

  post("/notes") { req: CreateNoteRequest =>
    val note = Note(None, req.title, req.content)
    noteRepo.insert(note).map { _ =>
      response.created.json(note)
    }
  }

}
