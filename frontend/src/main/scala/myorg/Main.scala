package myorg

import cats.effect.Async
import cats.effect.IO
import cats.syntax.all.*
import myorg.chart.Charting
import tyrian.Html.*
import tyrian.*

import scala.concurrent.duration.*
import scala.scalajs.js.annotation.*

import components.Header

@JSExportTopLevel("TyrianApp")
object Main extends TyrianApp[Msg, Model]:

  def main(args: Array[String]): Unit =
    // Note: this refers to the div with id=myApp in index.html
    launch("myApp")

  def router: Location => Msg =
    case internal: Location.Internal =>
      internal.pathName match
        case State.Home.pathName  => Msg.Home
        case State.Pause.pathName => Msg.Pause
        case _                    => Msg.NoOp

    case external: Location.External =>
      Msg.ExternalRedirect(external.url)

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Model.initial, Cmd.emit(Msg.RenderChart))

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    case Msg.Home =>
      (model.withState(State.Home), Cmd.None)

    case Msg.Pause =>
      (model.withState(State.Pause), Cmd.None)

    case Msg.ExternalRedirect(location) =>
      (model, Nav.loadUrl(location))

    case Msg.NoOp =>
      (model.withState(State.Home), Cmd.None);

    case Msg.Increment =>
      val cmd: Cmd[IO, Msg] = model.accessChart(_.update[IO](model.count))
      (model.updateCount(_ + 1), cmd)

    case Msg.RenderChart =>
      (
        model,
        // NOTE: we pass ownership of the chart back to Tyrian's model so that we can update it later
        Cmd.Run(
          Charting.initializeChart[IO]("myChartCanvas"),
          Msg.ReceiveChart(_)
        )
      )

    case Msg.ReceiveChart(chart) =>
      (
        model.withChart(chart),
        Cmd.None
      )

  def view(model: Model): Html[Msg] =
    div(
      Header.view,
      div(text(s"You are now at: ${model.state}")),
      div(id := "appCount")(text(s"Count: ${model.count}")),
      div(id := "myChart")(
        canvas(id := "myChartCanvas")()
      )
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    if (model.state == State.Home)
      Sub.every[IO](1.second).map(_ => Msg.Increment)
    else Sub.None

final case class Model(
    state: State,
    count: Int,
    chart: Option[Charting]
):
  def updateCount(fn: Int => Int): Model =
    copy(count = fn(count))

  def withState(state: State): Model =
    copy(state = state)

  def withChart(chart: Charting): Model =
    copy(chart = Some(chart))

  def accessChart[F[_]: Async](fn: Charting => F[Any]): Cmd[F, Msg] =
    chart.fold(ifEmpty = Cmd.None)(chart => Cmd.SideEffect[F](fn(chart).void))

object Model:
  val initial = Model(State.Home, 0, None)

enum State(val pathName: String):
  case Home  extends State("/")
  case Pause extends State("/pause")

enum Msg:
  case Home, Pause, NoOp
  case Increment
  case RenderChart
  case ReceiveChart(chart: Charting)
  case ExternalRedirect(location: String)
