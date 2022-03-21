package spacenorm

import spacenorm.Behaviours.*
import spacenorm.Positions.*
import scala.util.Random

case class Configuration(spaceWidth: Int, spaceHeight: Int, numberAgents: Int, numberBehaviours: Int, obstacleSide: Int, 
                         threshold: Double, maxMove: Double, obstacleTopLefts: List[Position], exits: List[Position]):

  def influenceFactor(distance: Double): Double =
    1 - distance / threshold

  val obstructed: List[Position] =
    obstacleTopLefts.flatMap{ topLeft =>
      for (x <- 0 until obstacleSide; y <- 0 until obstacleSide)
        yield (move(topLeft, x, y))
    }

  def randomExit: Position =
    exits(Random.nextInt(exits.size))

  def chooseVelocity(position: Position, goal: Goal, behaviour: Behaviour,
                     agents: List[Position], obstructed: List[Position]): Velocity =
    direction(position, goal).rotations.find { velocity =>
      var moved = velocity.moveFrom(position)
      validPosition(moved, this) && !agents.contains(moved) || !obstructed.contains(moved)
    }.getOrElse(Velocity(0, 0))

  def goalChoice(current: Goal, position: Position): Goal =
    if (current == position)
      randomPosition(this)
    else
      current

object Configuration:
  def createRandom(spaceWidth: Int, spaceHeight: Int, numberAgents: Int, numberBehaviours: Int,
                   numberObstacles: Int, obstacleSide: Int, numberExits: Int, threshold: Double, maxMove: Double) = {

    val obstacleTopLefts: List[Position] =
      List.fill(numberObstacles)(randomPosition(spaceWidth, spaceHeight))

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

    Configuration(spaceWidth, spaceHeight, numberAgents, numberBehaviours, obstacleSide, threshold, maxMove, obstacleTopLefts, exits)
  }

  def configuration1 =
    createRandom(
      spaceWidth       = 100,
      spaceHeight      = 100,
      numberAgents     = 1000,
      numberBehaviours = 2,
      numberObstacles  = 20,
      obstacleSide     = 5,
      numberExits      = 50,
      threshold        = 10.0,
      maxMove          = Math.sqrt(2)
    )
