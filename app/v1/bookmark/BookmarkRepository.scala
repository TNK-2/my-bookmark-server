package v1.bookmark

import akka.actor.ActorSystem
import play.api.MarkerContext
import play.api.libs.concurrent.CustomExecutionContext
import play.api.Logger

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

final case class BookmarkData(id: Int, title: String, url: String)

class BookmarkExecutionContext @Inject()(actorSystem: ActorSystem)
  extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait BookmarkRepository {
  def list()(implicit mc: MarkerContext): Future[Iterable[BookmarkData]]
  def add(br: BookmarkResource)(implicit mc: MarkerContext): Future[BookmarkData]
  def delete(id: Int)(implicit mc: MarkerContext): Future[Unit]
}

@Singleton
class BookmarkRepositoryImpl @Inject()()(implicit ec: BookmarkExecutionContext)
  extends BookmarkRepository {

  private val logger = Logger(this.getClass)

  private var bookmarks = List(
    BookmarkData(1, "title1", "http://localhost:9000/v1/bookmarks"),
    BookmarkData(2, "title1", "http://localhost:9000/v1/bookmarks"),
    BookmarkData(3, "title1", "http://localhost:9000/v1/bookmarks"),
    BookmarkData(4, "title1", "http://localhost:9000/v1/bookmarks"),
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[BookmarkData]] = {
    Future {
      logger.trace(s"list: ")
      bookmarks
    }
  }

  override def add(br: BookmarkResource)(implicit mc: MarkerContext): Future[BookmarkData] = {
    Future {
      val data = BookmarkData(br.id, br.title, br.url)
      bookmarks :+= data
      data
    }
  }

  override def delete(id: Int)(implicit mc: MarkerContext): Future[Unit] = Future {
    bookmarks = bookmarks.filterNot(_.id == id)
  }

}
