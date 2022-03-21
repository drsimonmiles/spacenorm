package spacenorm

import scala.util.Random
import spacenorm.Agents.*
import spacenorm.Positions.*
import spacenorm.Velocity.*

object Configuration:
  val spaceWidth       = 100
  val spaceHeight      = 100
  val numberAgents     = 1000
  val numberBehaviours = 2
  val numberObstacles  = 20
  val obstacleSide     = 5
  val numberExits      = 50
  val threshold        = 10.0
  val maxMove          = Math.sqrt(2)
  
  def influenceFactor(distance: Double): Double =
    1 - distance / threshold

  def distanceBetween(agent1: Agent, agent2: Agent, state: State): Double =
    distance(state.position(agent1), state.position(agent2))
