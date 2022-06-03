package sim

import scala.util.Random
import spacenorm.*

object Generate:
  def chooseGoal(current: Goal, position: Position, config: Configuration): Goal =
    if (current == position)
      randomValidPosition(config)
    else
      current

  /** Generate a single run's configuration from the settings fixed across all runs by
  randomly choosing obstacle and exit positions . */
  def newRunConfiguration(settings: Settings): Configuration = {
    import settings.*
    val obstacleTopLefts: List[Position] =
      List.fill(numberObstacles)(randomPosition(spaceWidth, spaceHeight))

    val possibleExits: List[Position] = {
      val xSides = for (x <- 0 until spaceWidth) yield List(Position(x, 0), Position(x, spaceHeight - 1))
      val ySides = for (y <- 0 until spaceHeight) yield List(Position(0, y), Position(spaceWidth - 1, y))
      val oSides =
        obstacleTopLefts.flatMap(pos =>
          (for (d <- 0 until obstacleSide)
            yield List(pos.move(d, -1), pos.move(d, obstacleSide), pos.move(-1, d), pos.move(obstacleSide, d)))
        ) 
      (xSides ++ ySides ++ oSides).flatten.toList
    }

    val exits: List[Position] = List.fill(numberExits) {
      possibleExits(Random.nextInt(possibleExits.size))
    }

    Configuration(spaceWidth, spaceHeight, numberAgents, numberBehaviours, obstacleSide, threshold, distanceInfluence, maxMove, obstacleTopLefts, exits)
  }

  private var nextID: Int = 0
  def nextAgent: Agent = {
    nextID += 1
    Agent(nextID)
  }

  def nextAgents(count: Int): List[Agent] =
    List.fill(count)(nextAgent)

  /**
   * Generate the initial state of a simulation run, given the static configuration.
   */
  def newState(config: Configuration): State = {
    val agents    = nextAgents(config.numberAgents)
    val behaviour = agents.map { agent => (agent, randomBehaviour(config)) }.toMap
    val position  = agents.map { agent => (agent, randomValidPosition(config)) }.toMap
    val goal      = agents.map { agent => (agent, randomValidPosition(config)) }.toMap
    val successes = agents.map { agent => (agent, 0.0) }.toMap

    State(config, agents, behaviour, position, goal, successes)
  }

  def randomBehaviour(configuration: Configuration): Behaviour =
    Behaviour(Random.nextInt(configuration.numberBehaviours))

  def randomExit(config: Configuration): Position =
    config.validExits(Random.nextInt(config.validExits.size))

  def randomPosition(width: Int, height: Int): Position =
    Position(Random.nextInt(width), Random.nextInt(height))

  def randomValidPosition(config: Configuration): Position = {
    val position = randomPosition(config.spaceWidth, config.spaceHeight)
    if (config.validAgentPosition(position))
      position
    else
      randomValidPosition(config)
  }
