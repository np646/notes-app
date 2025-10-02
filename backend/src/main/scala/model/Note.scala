package model

import reactivemongo.api.bson.{
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  Macros
}

// Database model
case class Note(_id: Option[BSONObjectID], title: String, content: String)

// MongoDB uses BSON for serialization
object Note {
  implicit val reader: BSONDocumentReader[Note] = Macros.reader[Note]
  implicit val writer: BSONDocumentWriter[Note] = Macros.writer[Note]
}

case class NoteResponse(id: String, title: String, content: String)

object NoteResponse {
  def from(note: Note): NoteResponse = NoteResponse(
    id = note._id.map(_.stringify).getOrElse(""),
    title = note.title,
    content = note.content
  )
}
