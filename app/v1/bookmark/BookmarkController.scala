package v1.bookmark

import play.api.{Logger, Logging}
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._
import play.api.libs.json.{JsValue, Json}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class BookmarkFormInput(title: String, url: String)

case class BookmarkControllerComponents @Inject()(
   bookmarkResourceHandler: BookmarkResourceHandler,
   actionBuilder: DefaultActionBuilder,
   parsers: PlayBodyParsers,
   messagesApi: MessagesApi,
   langs: Langs,
   fileMimeTypes: FileMimeTypes,
   executionContext: scala.concurrent.ExecutionContext)
  extends ControllerComponents

class BookmarkBaseController @Inject()(bcc: BookmarkControllerComponents)
    extends BaseController {
  override protected def controllerComponents: ControllerComponents = bcc
  def bookmarkResourceHandler = bcc.bookmarkResourceHandler
  def BookmarkAction = bcc.actionBuilder
}

class BookmarkAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) with Logging {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    logger.info("Calling BookmarkAction")
    block(request)
  }
}

@Singleton
class BookmarkController @Inject()(bcc: BookmarkControllerComponents)(implicit ec:ExecutionContext)
    extends BookmarkBaseController(bcc) {

  private val logger = Logger(getClass)

//  def index = Action {
//    Ok("aaaaaa")
//  }

  def index: Action[AnyContent] = BookmarkAction.async { implicit request =>
    logger.trace("BookmarkController::index")
    print("BookmarkController::index")
    bookmarkResourceHandler.list.map { implicit bookmarks =>
      Ok(Json.toJson(bookmarks))
    }
  }

}
