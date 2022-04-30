package viz

import org.scalajs.dom
import org.scalajs.dom.*
import org.scalajs.dom.html.*
import org.scalajs.dom.raw.*
import scala.scalajs.js.annotation.JSExportTopLevel
import spacenorm.State

object Main {
  def main(args: Array[String]): Unit = {
    val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas]
    val draw: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    val trace: List[State] = Nil

    canvas.width = 2107
    canvas.height = 1801

    //val assets: AssetsSet = AssetsSet ()
    //assets.add (StrandMap, dom.document, "/resources/OSM-Aldwych.png")
    //assets.add (SaveIcon,  dom.document, "/resources/save-file.png")
    //assets.add (LoadIcon,  dom.document, "/resources/load-file.png")

    //canvas.onclick = { (event: MouseEvent) =>
    //}
    //canvas.onmousedown = { (event: MouseEvent) =>
    //}
    //canvas.onmousemove = { (event: MouseEvent) =>
    //}
    //canvas.onmouseup = { (event: MouseEvent) =>
    //}
    //canvas.ondblclick = { (event: MouseEvent) =>
    //}

    def run(): Unit =
      //if (assets.loaded) {
      //}
      ???

    dom.window.setInterval(() => run(), 10)
  }

  @JSExportTopLevel("load")
  def loadFromFile(): Unit = {
    val input = dom.document.getElementById("file-input").asInstanceOf[Input]
    input.onchange = { (event: Event) =>
      val file = input.files(0)
      val reader = new FileReader()
      reader.readAsText(file,"UTF-8")
      reader.onload = readerEvent => {
        var content = reader.result.toString
        /*decode[Area](content)(Encode.decodeArea) match {
          case Left(error) =>
            dom.window.alert("Invalid data or other loading problem")
          case Right(result) =>
            state = state.copy (areaView = AreaView(result, None))
            syncState
        }*/
      }
    }
    input.click
  }
}

@JSExportTopLevel("step")
def step(): Unit =
  ???

@JSExportTopLevel("run")
def run(): Unit =
  ???
