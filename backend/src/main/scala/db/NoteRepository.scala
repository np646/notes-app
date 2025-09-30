package db

import model.Note
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.Cursor
import reactivemongo.api.commands.WriteResult

import scala.concurrent.{ExecutionContext, Future}

class NoteRepository(mongo: MongoManager, dbName: String)(implicit ec: ExecutionContext) {
  private def collF: Future[BSONCollection] =
    mongo.database(dbName).map(_.collection("notes"))

  def insert(note: Note): Future[WriteResult] =
    collF.flatMap(_.insert.one(note))

  def findByTitle(title: String): Future[Seq[Note]] =
    collF.flatMap { coll =>
      val selector: BSONDocument = BSONDocument("title" -> title)
      coll.find(selector, Option.empty[BSONDocument])
        .cursor[Note]()
        .collect[Seq](Int.MaxValue, Cursor.FailOnError[Seq[Note]]())
    }

    def findAll(): Future[Seq[Note]] =
    collF.flatMap { coll =>
      coll.find(BSONDocument.empty, Option.empty[BSONDocument])
        .cursor[Note]()
        .collect[Seq](Int.MaxValue, Cursor.FailOnError[Seq[Note]]())
    }
}
