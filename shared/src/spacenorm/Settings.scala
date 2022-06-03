package spacenorm

/*
 * For simulation without exit/entry, set numberExits = 0
 * For simulation without obstacles, set numberObstacles = 0
 * For simulation without movement, set maxMove = 0.0
 */
final case class Settings(statsOutput: String, traceOutputPrefix: String, numberRuns: Int, numberTraces: Int,
                          numberTicks: Int, spaceWidth: Int, spaceHeight: Int, numberAgents: Int, numberBehaviours: Int,
                          numberObstacles: Int, obstacleSide: Int, numberExits: Int, threshold: Double, maxMove: Double)
