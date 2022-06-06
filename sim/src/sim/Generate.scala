package sim

import scala.util.Random
import spacenorm.*

object Generate:
  def chooseGoal(current: Goal, position: Position, config: Configuration, random: Random): Goal =
    if (current == position)
      randomValidPosition(config, random)
    else
      current

  /** Generate a single run's configuration from the settings fixed across all runs by
  randomly choosing obstacle and exit positions . */
  def newRunConfiguration(settings: Settings, random: Random): Configuration = {
    import settings.*

    def withinObstacle(obstacle: Position, point: Position): Boolean =
      point.x >= obstacle.x && point.x < obstacle.x + obstacleSide &&
        point.y >= obstacle.y && point.y < obstacle.y + obstacleSide

    def obstacleBorder(topLeft: Position): List[Position] =
      (for (d <- -1 until obstacleSide)
        yield List(topLeft.move(d, -1), topLeft.move(d, obstacleSide), topLeft.move(-1, d), topLeft.move(obstacleSide, d)))
        .toList.flatten

    def overlaps(obstacles: List[Position], newObstacle: Position): Boolean =
      obstacleBorder(newObstacle).exists(corner => obstacles.exists(existing => withinObstacle(existing, corner)))

    def generateObstacles(soFar: List[Position]): List[Position] =
      if (soFar.size == numberObstacles)
        soFar
      else {
        val next = randomPosition(spaceWidth - obstacleSide - 1, spaceHeight - obstacleSide - 1, random).move(1, 1)
        generateObstacles(if (overlaps(soFar, next)) soFar else next :: soFar)
      }

    val obstacleTopLefts: List[Position] = generateObstacles(Nil)

    val possibleExits: List[Position] = {
      val xSides = for (x <- 0 until spaceWidth) yield List(Position(x, 0), Position(x, spaceHeight - 1))
      val ySides = for (y <- 0 until spaceHeight) yield List(Position(0, y), Position(spaceWidth - 1, y))
      val oSides = obstacleTopLefts.map(obstacleBorder)
      (xSides ++ ySides ++ oSides).flatten.toList
    }

    val exits: List[Position] = List.fill(numberExits) {
      possibleExits(random.nextInt(possibleExits.size))
    }

    val distanced =
      Configuration(spaceWidth, spaceHeight, numberAgents, numberBehaviours, obstacleSide, threshold,
                    distanceInfluence, netConstruction, transmission, maxMove, obstacleTopLefts, exits, None)

    netConstruction match {
      case Networker.Distance => distanced
      case Networker.Random   => randomMatchingNetwork(newState(distanced, random), random)
    }    
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
  def newState(config: Configuration, random: Random): State = {
    val agents    = config.agentsInNetwork.getOrElse(nextAgents(config.numberAgents))
    val behaviour = agents.map { agent => (agent, randomBehaviour(config, random)) }.toMap
    val position  = agents.map { agent => (agent, randomValidPosition(config, random)) }.toMap
    val goal      = agents.map { agent => (agent, randomValidPosition(config, random)) }.toMap
    val successes = agents.map { agent => (agent, 0.0) }.toMap
    
    State(config, agents, behaviour, position, goal, successes)
  }

  def randomAgent(state: State, random: Random): Agent =
    state.agents(random.nextInt(state.agents.size))

  def randomBehaviour(config: Configuration, random: Random): Behaviour =
    Behaviour(random.nextInt(config.numberBehaviours))

  def randomExit(config: Configuration, random: Random): Position =
    config.validExits(random.nextInt(config.validExits.size))

  def randomPosition(width: Int, height: Int, random: Random): Position =
    Position(random.nextInt(width), random.nextInt(height))

  def randomValidPosition(config: Configuration, random: Random): Position = {
    val position = randomPosition(config.spaceWidth, config.spaceHeight, random)
    if (config.validAgentPosition(position))
      position
    else
      randomValidPosition(config, random)
  }

  /** 
   * Generate a random network with the same number of edges as in the given simulation state.
  */
  def randomMatchingNetwork(state: State, random: Random): Configuration = {
    val numberEdges = state.agents.flatMap {
      agent1 => state.neighbours(agent1).map(agent2 => Set(agent1, agent2))
    }.toSet.size
    
    def generateEdges(soFar: Set[Set[Agent]]): Set[Set[Agent]] =
      if (soFar.size == numberEdges)
        soFar
      else {
        val agent1 = randomAgent(state, random)
        val agent2 = randomAgent(state, random)
        generateEdges(if (agent1 == agent2) soFar else soFar + Set(agent1, agent2))
      }

    val edges = generateEdges(Set.empty)
    val network =
      state.agents.map { agent1 =>
        (agent1, state.agents.filter(agent2 => edges.contains(Set(agent1, agent2))))
      }.toMap
    
    state.config.copy(network = Some(network))
  }
