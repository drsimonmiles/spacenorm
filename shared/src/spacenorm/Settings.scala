package spacenorm

final case class Settings(statsOutput: String, traceOutputPrefix: String, numberRuns: Int, numberTraces: Int,
                          numberTicks: Int, spaceWidth: Int, spaceHeight: Int, numberAgents: Int, numberBehaviours: Int,
                          numberObstacles: Int, obstacleSide: Int, numberExits: Int, threshold: Double,
                          distanceInfluence: Influence, maxMove: Double)
