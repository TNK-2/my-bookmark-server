package v1.bookmark

import play.api.MarkerContext
import play.api.libs.json.{Format, Json}

import javax.inject.{Inject, Provider}
import scala.concurrent.{ExecutionContext, Future}

case class BookmarkResource(id: Int, title: String, url: String)

object BookmarkResource {
  implicit val format: Format[BookmarkResource] = Json.format
}

class BookmarkResourceHandler @Inject()(
  routerProvider: Provider[BookmarkRouter],
  bookmarkRepository: BookmarkRepository)(implicit ec: ExecutionContext) {

  def list(implicit mc: MarkerContext): Future[Iterable[BookmarkResource]] = {
    bookmarkRepository.list().map { bookmarks =>
      bookmarks.map(bookmarkData => this.createBookmarkResource(bookmarkData))
    }
  }

  private def createBookmarkResource(b: BookmarkData): BookmarkResource = {
    BookmarkResource(id = b.id, title = b.title, url = b.url)
  }
}
