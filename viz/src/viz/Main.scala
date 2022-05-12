package viz

import org.scalajs.dom
import org.scalajs.dom.*
import org.scalajs.dom.html.{Canvas, Input}
import org.scalajs.dom.raw.*
import scala.scalajs.js.annotation.JSExportTopLevel
import spacenorm.State
import spacenorm.Configuration
import viz.AgentView.showAgent
import viz.LogDecode.{openNewRun, loadNextState, restartRun}

object Main {
  val cellSize = 10

  val reader: LogReader = new LogReader
  var config: Option[Configuration] = None
  var playing: Boolean = false

  def main(args: Array[String]): Unit = {
    dom.window.setInterval(() => stepIfPlaying(), 1000)
  }

  @JSExportTopLevel("load")
  def loadFromFile(): Unit = {
    config = openNewRun(reader)
    config.foreach { loaded =>
      val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas]
      canvas.width  = loaded.spaceWidth  * cellSize
      canvas.height = loaded.spaceHeight * cellSize
      playing = false
      step()
    }
  }

  @JSExportTopLevel("step")
  def step(): Unit = {
    config.foreach { loaded =>
      loadNextState(reader, loaded).map { state =>
        val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas] 
        val draw: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
        state.agents.foreach { agent =>
          showAgent(state.position(agent), draw)
        }
      }
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