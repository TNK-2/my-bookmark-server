package v1.bookmark

import net.logstash.logback.marker.LogstashMarker
import play.api.{Logger, Logging, MarkerContext}
import play.api.http.{FileMimeTypes, HttpVerbs}
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{ActionBuilder, ActionBuilderImpl, AnyContent, BaseController, BodyParser, BodyParsers, ControllerComponents, DefaultActionBuilder, MessagesRequestHeader, PlayBodyParsers, PreferredMessagesProvider, Request, RequestHeader, Result, WrappedRequest}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait RequestMarkerContext {

  import net.logstash.logback.marker.Markers

  private def marker(tuple: (String, Any)) = Markers.append(tuple._1, tuple._2)

  private implicit class RichLogstashMarker(marker1: LogstashMarker) {
    def &&(marker2: LogstashMarker): LogstashMarker = marker1.and(marker2)
  }

  implicit def requestHeaderToMarkerContext(implicit request: RequestHeader): MarkerContext = {
    MarkerContext {
      marker("id" -> request.id) && marker("host" -> request.host) && marker(
        "remoteAddress" -> request.remoteAddress)
    }
  }
}

trait BookmarkRequestHeader
  extends MessagesRequestHeader
    with PreferredMessagesProvider

case class BookmarkRequest[A](request: Request[A], val messagesApi: MessagesApi)
  extends WrappedRequest(request)
    with BookmarkRequestHeader

class BookmarkAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser)
    with Logging {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    logger.info("Calling BookmarkAction")
    block(request)
  }
}

class BookmarkActionBuilder @Inject()
(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)
(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[BookmarkRequest, AnyContent]
    with RequestMarkerContext
    with HttpVerbs {

  override val parser: BodyParser[AnyContent] = playBodyParsers.anyContent

  type PostRequestBlock[A] = BookmarkRequest[A] => Future[Result]

  private val logger = Logger(this.getClass)

  override def invokeBlock[A](request: Request[A],
                              block: PostRequestBlock[A]): Future[Result] = {
    // Convert to marker context and use request in block
    implicit val markerContext: MarkerContext = requestHeaderToMarkerContext(
      request)
    logger.trace(s"invokeBlock: ")

    val future = block(new BookmarkRequest[A](request, messagesApi))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }
}