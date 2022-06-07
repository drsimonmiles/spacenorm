package spacenorm

/** Settings loaded from the model configuration file. */
final case class Settings(statsOutput: String,
                          traceOutputPrefix: String,
                          numberRuns: Int,
                          numberTraces: Int,
                          numberTicks: Int,
                          spaceWidth: Int,
                          spaceHeight: Int,
                          numberAgents: Int,
                          numberBehaviours: Int,
                          numberObstacles: Int,
                          obstacleSide: Int,
                          numberExits: Int,
                          threshold: Double,
                          distanceInfluence: Influence,
                          netConstruction: Networker,
                          transmission: Transmission,
                          maxMove: Double,
                          randomSeed: Long)
