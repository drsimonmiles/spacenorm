package spacenorm

final case class Settings(spaceWidth: Int, spaceHeight: Int, numberAgents: Int, numberBehaviours: Int,
                          numberObstacles: Int, obstacleSide: Int, numberExits: Int, threshold: Double, maxMove: Double)
