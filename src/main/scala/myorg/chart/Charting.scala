package myorg.chart

import cats.effect.*
import cats.syntax.all.*
import org.scalajs.dom.document
import org.scalajs.dom.html
import typings.chartJs.mod.*
import typings.moment.mod.Moment

import scala.scalajs.js
import scala.util.Random

/** Charting is a wrapper around the Chart.js library (which are Scalably
  * Typed's mappings)
  *
  * @param underlying
  *   the Chart.js chart
  */
final class Charting private (private val underlying: Chart):
  def update[F[_]: Sync](count: Int): F[Unit] =
    val chartData = ChartData()
      .setLabels(Charting.chartLabels)
      .setDatasets(
        js.Array(
          ChartDataSets()
            .setLabel("Some interesting stat")
            .setData(
              js.Array
                .apply(
                  Range
                    .Inclusive(count - 7, count + 7, 1)
                    .map(_.toDouble)
                    .map(each => Random.between(each, each + 7))
                    .toList*
                )
            )
        )
      )

    val setData = Sync[F].delay(underlying.data = chartData)
    val updateChart =
      val FiveHundredMillis = 500
      Sync[F]
        .delay(
          underlying.update(ChartUpdateProps().setDuration(FiveHundredMillis))
        )
        .void

    setData >> updateChart

object Charting:
  private val chartLabels: js.Array[
    String | js.Array[js.Date | Double | Moment | String] | Double | js.Date |
      Moment
  ] =
    js.Array(
      "January",
      "February",
      "March",
      "April",
      "May",
      "June",
      "July"
    )

  /** Initialize a chart given a canvas element identifier
    *
    * @param canvasElementId
    *   is the identifier of the canvas element
    * @param async
    *   is the Async instance required to suspend side effects
    * @return
    */
  def initializeChart[F[_]](canvasElementId: String)(using
      async: Async[F]
  ): F[Charting] =

    // NOTE: this accesses the real DOM
    val fetchCanvasElementById =
      async.delay {
        Option(document.getElementById(canvasElementId)).collect {
          case e: html.Canvas => e
        }
      }

    fetchCanvasElementById
      .flatMap {
        case None =>
          // In the case where the canvas element is not found, we yield control back to the event loop and try again in the hope that the canvas element will be found once the runtime
          async.cede >> initializeChart(canvasElementId)

        case Some(canvasElement) =>
          val chartConfig =
            ChartConfiguration()
              .setType(ChartType.bar)
              .setData(
                ChartData()
                  .setLabels(chartLabels)
                  .setDatasets(
                    js.Array(
                      ChartDataSets()
                        .setLabel("Some interesting stat")
                        .setData(js.Array(65, 59, 80, 81, 56, 55, 40))
                    )
                  )
              )
          async
            .delay(Chart.apply.newInstance2(canvasElement, chartConfig))
            .map(Charting(_))
      }

    /** Update the chart with new data
      *
      * @param chart
      *   is the chart to update
      * @param count
      *   is the seed that is used to generate the new data
      * @return
      */
