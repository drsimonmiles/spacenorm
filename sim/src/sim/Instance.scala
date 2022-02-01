package sim

import scala.util.Random
import sim.Behaviours.Behaviour
import sim.Configuration._
import sim.Positions._

class Instance:
  val obstacleTopLefts: List[Position] =
    List.fill(numberObstacles)(randomPosition)

  val obstructed: List[Position] =
    obstacleTopLefts.flatMap{ topLeft =>
      for (x <- 0 until obstacleSide; y <- 0 until obstacleSide)
        yield (move(topLeft, x, y))
    }

  val possibleExits: List[Position] = {
    val xSides = for (x <- 0 until spaceWidth) yield List(at(x, 0), at(x, spaceHeight - 1))
    val ySides = for (y <- 0 until spaceHeight) yield List(at(0, y), at(spaceWidth - 1, y))
    val oSides =
      obstacleTopLefts.flatMap(pos =>
        (for (d <- 0 until obstacleSide)
          yield List(move(pos, d, -1), move(pos, d, obstacleSide), move(pos, -1, d), move(pos, obstacleSide, d)))
      )
    (xSides ++ ySides ++ oSides).flatten.toList
  }

  val exits: List[Position] = List.fill(numberExits) {
    possibleExits(Random.nextInt(possibleExits.size))
  }

  def randomExit: Position =
    exits(Random.nextInt(exits.size))

  def chooseVelocity(position: Position, goal: Goal, behaviour: Behaviour,
                     agents: List[Position], obstructed: List[Position]): Velocity =
    direction(position, goal).rotations.find { velocity =>
      var moved = velocity.moveFrom(position)
      validPosition(moved) && !agents.contains(moved) || !obstructed.contains(moved)
    }.getOrElse(Velocity(0, 0))

  def goalChoice(current: Goal, position: Position): Goal =
    if (current == position)
      randomPosition
    else
      current