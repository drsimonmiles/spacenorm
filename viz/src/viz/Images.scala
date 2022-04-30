package viz

import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.Document
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.HTMLImageElement

object Images {
  def coloured (element: HTMLImageElement, document: Document, colour: String): Canvas = {
    val buffer = document.createElement("canvas").asInstanceOf[Canvas]
    buffer.width  = element.width
    buffer.height = element.height
    val draw: CanvasRenderingContext2D = buffer.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    draw.drawImage(element, 0, 0)
    draw.fillStyle = colour
    draw.globalCompositeOperation = "source-atop"
    draw.fillRect(0, 0, buffer.width, buffer.height)
    buffer
  }

}