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
  def loadFromFile(): Unit =
    openNewRun(reader)

  def newRunLoaded(newConfig: Configuration): Unit = {
    config = Some(newConfig)
    println(newConfig.toString)
    val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas]
    canvas.width  = newConfig.spaceWidth  * cellSize
    canvas.height = newConfig.spaceHeight * cellSize
    playing = false
    step()
  }

  @JSExportTopLevel("step")
  def step(): Unit =
    config.foreach { loadNextState(reader, _) }

  def nextStepLoaded(state: State): Unit =
    config.foreach { loaded =>
      val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas] 
      val draw: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      draw.fillStyle = "black"
      draw.fillRect(0, 0, loaded.spaceWidth  * cellSize, loaded.spaceHeight * cellSize)
      state.agents.foreach { agent =>
        showAgent(state.position(agent), draw)
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