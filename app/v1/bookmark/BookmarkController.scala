package v1.bookmark

import play.api.Logger
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.{Inject, Singleton}

case class BookmarkFormInput(title: String, url: String)
case class BookmarkResource(title: String, url: String)

@Singleton
class BookmarkController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  private val logger = Logger(getClass)

  def index() = Action { request =>
    logger.trace("BookmarkController::index")
    Ok("OK")
  }
}
