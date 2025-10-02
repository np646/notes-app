package controllers

import com.twitter.finatra.http.Controller
import com.twitter.finagle.http.Request
import db.NoteRepository
import model.{Note, NoteResponse}
import reactivemongo.api.bson.BSONObjectID
import scala.concurrent.ExecutionContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

case class NoteRequest(title: String, content: String)

class NoteController(noteRepo: NoteRepository)(implicit ec: ExecutionContext)
    extends Controller {

  // Simple JSON parser
  private val jsonMapper = new ObjectMapper()
  jsonMapper.registerModule(DefaultScalaModule)

  // CORS preflight for /notes
  options("/notes") { _: Request =>
    response.ok
      .header("Access-Control-Allow-Origin", "*")
      .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
      .header("Access-Control-Allow-Headers", "Content-Type")
  }

  // CORS preflight for /notes/:id
  options("/notes/:id") { _: Request =>
    response.ok
      .header("Access-Control-Allow-Origin", "*")
      .header("Access-Control-Allow-Methods", "GET, PUT, DELETE, OPTIONS")
      .header("Access-Control-Allow-Headers", "Content-Type")
  }

  // Get all notes
  get("/notes") { _: Request =>
    noteRepo.findAll().map { notes =>
      val jsonSafe = notes.map(NoteResponse.from)
      response.ok
        .json(jsonSafe)
        .header("Access-Control-Allow-Origin", "*")
    }
  }

  // Get one note by ID
  get("/notes/:id") { request: Request =>
    val id = request.params("id")
    noteRepo.findById(id).map {
      case Some(note) =>
        response.ok
          .json(NoteResponse.from(note))
          .header("Access-Control-Allow-Origin", "*")
      case None =>
        response.notFound
          .json(Map("error" -> "Note not found"))
          .header("Access-Control-Allow-Origin", "*")
    }
  }

  // Insert a new note
  post("/notes") { req: NoteRequest =>
    val note = Note(Some(BSONObjectID.generate()), req.title, req.content)
    noteRepo.insert(note).map { _ =>
      response.created
        .json(NoteResponse.from(note))
        .header("Access-Control-Allow-Origin", "*")
    }
  }

  // Update a note by ID
  put("/notes/:id") { request: Request =>
    val id = request.params("id")
    val req = jsonMapper.readValue(request.contentString, classOf[NoteRequest])

    noteRepo
      .update(id, req.title, req.content)
      .map { _ =>
        response.ok
          .json(Map("message" -> "Note updated"))
          .header("Access-Control-Allow-Origin", "*")
      }
      .recover { case ex: Exception =>
        response.badRequest
          .json(Map("error" -> ex.getMessage))
          .header("Access-Control-Allow-Origin", "*")
      }
  }

  // Delete a note by ID
  delete("/notes/:id") { request: Request =>
    val id = request.params("id")

    noteRepo
      .delete(id)
      .map { _ =>
        response.ok
          .json(Map("message" -> "Note deleted"))
          .header("Access-Control-Allow-Origin", "*")
      }
      .recover { case ex: Exception =>
        response.badRequest
          .json(Map("error" -> ex.getMessage))
          .header("Access-Control-Allow-Origin", "*")
      }
  }
}
