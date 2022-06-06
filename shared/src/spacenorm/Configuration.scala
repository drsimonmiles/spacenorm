package spacenorm

/**
 * Instances of this class hold the static configuration for a single run of the simulation.
 */
final case class Configuration(spaceWidth: Int,
                               spaceHeight: Int,
                               numberAgents: Int,
                               numberBehaviours: Int,
                               obstacleSide: Int, 
                               threshold: Double,
                               distanceInfluence: Influence,
                               netConstruction: Networker, 
                               transmission: Transmission,
                               maxMove: Double,
                               obstacleTopLefts: List[Position],
                               exits: List[Position],
                               network: Option[Map[Agent, List[Agent]]]):

  /** The set of possible behaviours in this configuration. */
  val allBehaviours: List[Behaviour] = (0 until numberBehaviours).map(Behaviour.apply).toList
  
  /** If there is a fixed network, this gives the list of agents in it. */
  val agentsInNetwork: Option[List[Agent]] =
    network.map(neighbours => neighbours.toList.flatMap(a => a._1 :: a._2).distinct)

  /** A convenience variable giving the positions that are blocked by an obstactle. */
  val obstructed: List[Position] =
    obstacleTopLefts.flatMap{ topLeft =>
      for (x <- 0 until obstacleSide; y <- 0 until obstacleSide)
        yield (topLeft.move(x, y))
    }

  /** Determines whether the given position is one that an agent could occupy in the simulated space, i.e. within
      the space bounds and not obstructed. */
  def validAgentPosition(position: Position): Boolean =
    position.x >= 0 && position.x < spaceWidth &&
      position.y >= 0 && position.y < spaceHeight &&
      !obstructed.contains(position)

  /** The exits that are not obstructed and so can actually be used to enter or exit the space. */
  val validExits = exits.filter(validAgentPosition)

  def accessible(from: Position, to: Position): Boolean =
    transmission.accessible(from, to, threshold, obstructed.contains)

  /** The influence factor at a given distance. */
  def influenceFactor(distance: Double): Double =
    distanceInfluence.influenceFactor(distance, threshold)
