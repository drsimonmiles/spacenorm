package viz

import org.scalajs.dom.CanvasRenderingContext2D
import spacenorm.Behaviour
import spacenorm.Position

object AgentView:
  def showAgent(position: Position, behaviour: Behaviour, draw: CanvasRenderingContext2D): Unit = {
    val point = Projection.project(position)
    draw.beginPath
    draw.arc(point.x, point.y, 5, 0, Math.PI * 2)
    behaviour.choice match {
      case 0 => draw.fillStyle = "red"
      case 1 => draw.fillStyle = "blue"
      case _ => draw.fillStyle = "green"
    }
    draw.fill
  }