package myorg

import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.*
import com.comcast.ip4s.*

object App extends IOApp.Simple:

  def static(file: String, request: Request[IO]) =
    StaticFile.fromResource("/" + file, Some(request)).getOrElseF(NotFound())

  val allowedFileTypes =
    List(".js", ".css", ".html", ".map", ".webm", ".png", ".ico")

  val httpRoutes = HttpRoutes
    .of[IO] {
      case request @ GET -> Root =>
        static("index.html", request)

      case request @ GET -> path
          if allowedFileTypes.exists(path.renderString.endsWith) =>
        static(path.renderString, request)
    }

  def run: IO[Unit] =
    val serverResource =
      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpRoutes.orNotFound)
        .build

    serverResource
      .use(_ => IO.never)
