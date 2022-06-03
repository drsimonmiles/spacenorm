package viz

import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.CanvasRenderingContext2D
import scala.scalajs.js.annotation.JSExportTopLevel
import spacenorm.{Configuration, State}
import viz.LogDecode.{openNewRun, loadNextState, restartRun}
import viz.View.{cellSize, distinctColours, showState}

object Main {
  val reader: LogReader = new LogReader
  var config: Option[Configuration] = None
  var playing: Boolean = false
  var agentColours: List[String] = Nil

  def main(args: Array[String]): Unit = {
    dom.window.setInterval(() => stepIfPlaying(), 100)
  }

  @JSExportTopLevel("load")
  def loadFromFile(): Unit =
    openNewRun(reader)

  def newRunLoaded(newConfig: Configuration): Unit = {
    config = Some(newConfig)
    val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas]
    canvas.width  = newConfig.spaceWidth  * cellSize
    canvas.height = newConfig.spaceHeight * cellSize
    agentColours = distinctColours(newConfig.numberBehaviours)
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
      showState(state, loaded, agentColours, draw)
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