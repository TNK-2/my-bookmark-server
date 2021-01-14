package v1.bookmark

import play.api.data.Form
import play.api.Logger
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class BookmarkFormInput(title: String, url: String)

case class BookmarkControllerComponents @Inject()
(
  bookmarkActionBuilder: BookmarkActionBuilder,
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

  def BookmarkAction = bcc.bookmarkActionBuilder
}

@Singleton
class BookmarkController @Inject()(bcc: BookmarkControllerComponents)(implicit ec:ExecutionContext)
    extends BookmarkBaseController(bcc) {

  private val logger = Logger(getClass)

  private val form: Form[BookmarkFormInput] = {
    import play.api.data.Forms._
    Form(
      mapping(
        "title" -> nonEmptyText,
        "url" -> nonEmptyText
      )(BookmarkFormInput.apply)(BookmarkFormInput.unapply)
    )
  }

//  def index = Action {
//    Ok("aaaaaa")
//  }

  def index: Action[AnyContent] = BookmarkAction.async { implicit request =>
    logger.trace("BookmarkController::index")
    bookmarkResourceHandler.list.map { implicit bookmarks =>
      Ok(Json.toJson(bookmarks))
    }
  }

  def show(id: String): Action[AnyContent] = BookmarkAction.async { implicit request =>
    logger.trace("BookmarkController::show")
    bookmarkResourceHandler.lookup(id).map { bookmark =>
      Ok(Json.toJson(bookmark))
    }
  }

  def add: Action[AnyContent] = BookmarkAction.async { implicit request =>
    logger.trace("BookmarkController::add")
    def failure(badForm: Form[BookmarkFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }
    def success(input: BookmarkFormInput) = {
      bookmarkResourceHandler.add(this.inputToResource(input)).map { bookmark =>
        Created(Json.toJson(bookmark))
      }
    }
    form.bindFromRequest().fold(failure, success)
  }

  def delete(id: String): Action[AnyContent] = BookmarkAction.async { implicit request =>
    logger.trace("BookmarkController::delete id: %id".format(id))
    bookmarkResourceHandler.delete(id = id).map { _ => Ok("delete success")}
  }

  private def inputToResource(b: BookmarkFormInput): BookmarkResource = {
    BookmarkResource(id = 0, title = b.title, url = b.url)
  }
}
