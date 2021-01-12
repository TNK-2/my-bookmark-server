import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import v1.bookmark.{BookmarkRepository, BookmarkRepositoryImpl}
import net.codingwell.scalaguice.ScalaModule
import javax.inject.Singleton

class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule
  with ScalaModule {

  override def configure() = {
    bind[BookmarkRepository].to[BookmarkRepositoryImpl].in[Singleton]
  }
}
