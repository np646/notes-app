package db

import model.Note
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.Cursor
import reactivemongo.api.commands.WriteResult
import scala.concurrent.{ExecutionContext, Future}

class NoteRepository(mongo: MongoManager, dbName: String)(implicit
    ec: ExecutionContext
) {

  private def coll: Future[BSONCollection] =
    mongo.database(dbName).map(_.collection("notes-app"))

  // Insert a new note
  def insert(note: Note): Future[WriteResult] =
    coll.flatMap(_.insert.one(note))

  // Get all notes
  def findAll(): Future[Seq[Note]] =
    coll.flatMap { c =>
      c.find(BSONDocument.empty, Option.empty[BSONDocument])
        .cursor[Note]()
        .collect[Seq](Int.MaxValue, Cursor.FailOnError())
    }

  // Get one note by ID
  def findById(id: String): Future[Option[Note]] =
    coll.flatMap { c =>
      BSONObjectID.parse(id).toOption match {
        case Some(objectId) =>
          c.find(BSONDocument("_id" -> objectId), Option.empty[BSONDocument])
            .one[Note]
        case None =>
          Future.successful(None)
      }
    }

  // Update a note by ID
  def update(id: String, title: String, content: String): Future[WriteResult] =
    coll.flatMap { c =>
      BSONObjectID.parse(id).toOption match {
        case Some(objectId) =>
          val selector = BSONDocument("_id" -> objectId)
          val update = BSONDocument(
            "$set" -> BSONDocument(
              "title" -> title,
              "content" -> content
            )
          )
          c.update.one(selector, update)
        case None =>
          Future.failed(new Exception("Invalid ID"))
      }
    }

  // Delete a note by ID
  def delete(id: String): Future[WriteResult] =
    coll.flatMap { c =>
      BSONObjectID.parse(id).toOption match {
        case Some(objectId) =>
          c.delete.one(BSONDocument("_id" -> objectId))
        case None =>
          Future.failed(new Exception("Invalid ID"))
      }
    }
}
