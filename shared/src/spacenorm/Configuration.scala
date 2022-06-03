package spacenorm

/**
 * Instances of this class hold the static configuration for a single run of the simulation.
 */
final case class Configuration(spaceWidth: Int, spaceHeight: Int, numberAgents: Int, numberBehaviours: Int, obstacleSide: Int, 
                               threshold: Double, maxMove: Double, obstacleTopLefts: List[Position], exits: List[Position]):
  /** The set of possible behaviours in this configuration. */
  val allBehaviours: Set[Behaviour] = (0 until numberBehaviours).map(Behaviour.apply).toSet
  
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

  /** The influence factor at a given distance. */
  def influenceFactor(distance: Double): Double =
    1 - distance / threshold
