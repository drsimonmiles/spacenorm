package viz

import org.scalajs.dom.CanvasRenderingContext2D
import spacenorm.{Behaviour, Configuration, Position, State}
import viz.Projection.project

/** Functionality to show model-specific elements of the simulation state, constructing the visualisation display on
 * the webpage canvas.
 */
object View:
  val cellSize = 10

  def showState(state: State, config: Configuration, agentColours: List[String], draw: CanvasRenderingContext2D): Unit = {
    showBackground(config, draw)
    config.obstacleTopLefts.foreach { topLeft =>
      showObstacle(topLeft, config, draw)
    }
    config.validExits.foreach { topLeft =>
      showExit(topLeft, draw)
    }
    config.network.foreach { neighbours =>
      neighbours.toList.foreach { list =>
        val position1 = state.position(list._1)
        println(list._2.size)
        list._2.foreach { agent2 => showEdge(position1, state.position(agent2), draw) }
      }
    }
    state.agents.foreach { agent =>
      showAgent(state.position(agent), state.behaviour(agent), agentColours, draw)
    }
  }

  def showBackground(config: Configuration, draw: CanvasRenderingContext2D): Unit = {
    draw.fillStyle = "black"
    draw.fillRect(0, 0, config.spaceWidth * cellSize, config.spaceHeight * cellSize)
  }

  def distinctColours(required: Int): List[String] =
    (0 until required)
      .map(point => (point.toDouble / required))
      .map(hue => s"hsl(${(hue * 360).toInt},100%,50%)")
      .toList

  def showAgent(position: Position, behaviour: Behaviour, colours: List[String], draw: CanvasRenderingContext2D): Unit = {
    val point = project(position)
    draw.beginPath
    draw.arc(point.x + cellSize / 2, point.y + cellSize / 2, cellSize / 2, 0, Math.PI * 2)
    draw.fillStyle = colours(behaviour.choice)
    draw.fill
  }

  def showObstacle(position: Position, config: Configuration, draw: CanvasRenderingContext2D): Unit = {
    val point = project(position)
    draw.fillStyle = "white"
    draw.fillRect(point.x, point.y, project(config.obstacleSide), project(config.obstacleSide))
  }

  def showExit(position: Position, draw: CanvasRenderingContext2D): Unit = {
    val point = project(position)
    draw.fillStyle = "green"
    draw.fillRect(point.x, point.y, cellSize, cellSize)
  }

  def showEdge(position1: Position, position2: Position, draw: CanvasRenderingContext2D): Unit = {
    val point1 = project(position1)
    val point2 = project(position2)
    draw.beginPath
    draw.moveTo(point1.x + cellSize / 2, point1.y + cellSize / 2)
    draw.strokeStyle = "darkgrey"
    draw.lineTo(point2.x + cellSize / 2, point2.y + cellSize / 2)
    draw.stroke
  }