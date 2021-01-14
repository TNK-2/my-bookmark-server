package v1.bookmark

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import javax.inject.Inject

class BookmarkRouter @Inject()(controller: BookmarkController) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/") => controller.index
    case GET(p"/$id") => controller.show(id)
    case POST(p"/") => controller.add
    case DELETE(p"/$id") => controller.delete(id)
  }
}
