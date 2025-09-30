package model
import reactivemongo.api.bson.{
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  Macros
}

case class Note(_id: Option[BSONObjectID], title: String, content: String)

object Note {
  implicit val noteWriter: BSONDocumentWriter[Note] = Macros.writer[Note]
  implicit val noteReader: BSONDocumentReader[Note] = Macros.reader[Note]
}
