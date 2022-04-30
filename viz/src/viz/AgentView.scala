package viz

import org.scalajs.dom.CanvasRenderingContext2D
import spacenorm.Position

object AgentView:
  def showAgent(position: Position, draw: CanvasRenderingContext2D): Unit = {
    val point = Projection.project(position)
    draw.beginPath
    draw.arc(point.x, point.y, 10, 0, Math.PI * 2)
    draw.fillStyle = "hsl(0.0, 100%, 50%)"
    draw.fill
  }