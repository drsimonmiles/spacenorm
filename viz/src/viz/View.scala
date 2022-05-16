package viz

import org.scalajs.dom.CanvasRenderingContext2D
import spacenorm.Behaviour
import spacenorm.Position
import spacenorm.Configuration
import spacenorm.State

object View:
  val cellSize = 10

  def showState(state: State, config: Configuration, draw: CanvasRenderingContext2D): Unit = {
    showBackground(config, draw)
    config.obstacleTopLefts.foreach { topLeft =>
      showObstacle(topLeft, config, draw)
    }
    config.validExits.foreach { topLeft =>
      showExit(topLeft, config, draw)
    }
    state.agents.foreach { agent =>
      showAgent(state.position(agent), state.behaviour(agent), draw)
    }
  }

  def showBackground(config: Configuration, draw: CanvasRenderingContext2D): Unit = {
    draw.fillStyle = "black"
    draw.fillRect(0, 0, config.spaceWidth * cellSize, config.spaceHeight * cellSize)
  }

  def showAgent(position: Position, behaviour: Behaviour, draw: CanvasRenderingContext2D): Unit = {
    val point = Projection.project(position)
    draw.beginPath
    draw.arc(point.x + cellSize / 2, point.y + cellSize / 2, cellSize / 2, 0, Math.PI * 2)
    behaviour.choice match {
      case 0 => draw.fillStyle = "red"
      case 1 => draw.fillStyle = "blue"
      case _ => draw.fillStyle = "green"
    }
    draw.fill
  }

  def showObstacle(position: Position, config: Configuration, draw: CanvasRenderingContext2D): Unit = {
    val point = Projection.project(position)
    draw.fillStyle = "white"
    draw.fillRect(point.x, point.y, Projection.project(config.obstacleSide), Projection.project(config.obstacleSide))
  }

  def showExit(position: Position, config: Configuration, draw: CanvasRenderingContext2D): Unit = {
    val point = Projection.project(position)
    draw.fillStyle = "green"
    draw.fillRect(point.x, point.y, cellSize, cellSize)
  }