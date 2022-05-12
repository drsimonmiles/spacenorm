package viz

import org.scalajs.dom
import org.scalajs.dom.*
import org.scalajs.dom.html.{Canvas, Input}
import org.scalajs.dom.raw.*
import scala.scalajs.js.annotation.JSExportTopLevel
import spacenorm.State
import spacenorm.Configuration
import viz.LogDecode.{openNewRun, loadNextState, restartRun}

object Main {
  val reader: LogReader = new LogReader
  var config: Option[Configuration] = None
  var playing: Boolean = false

  def main(args: Array[String]): Unit = {
    val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas]
    val draw: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    val trace: List[State] = Nil

    canvas.width = 300
    canvas.height = 200

    dom.window.setInterval(() => stepIfPlaying(), 1000)
  }

  @JSExportTopLevel("load")
  def loadFromFile(): Unit = {
    config  = openNewRun(reader)
    // *** SET CANVAS TO RIGHT SIZE FOR THIS RUN ***
    playing = false
    step()
  }

  @JSExportTopLevel("step")
  def step(): Unit = {
    config.foreach { loaded =>
      val state = loadNextState(reader, loaded)
      // *** SHOW STATE ON SCREEN ***
    }
  }

  def stepIfPlaying(): Unit =
    if (playing) step()

  @JSExportTopLevel("run")
  def run(): Unit =
    playing = !playing

  @JSExportTopLevel("restart")
  def restart(): Unit =
    restartRun(reader)
}