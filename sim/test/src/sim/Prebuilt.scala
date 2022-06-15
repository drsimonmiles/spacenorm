package sim

import spacenorm.*

object Prebuilt:
  val obstaclesA = List(Position(1, 1), Position(94, 1), Position(1, 94), Position(94, 94))
  val exitsA = List(Position(0, 0), Position(50, 0), Position(93, 97))
  val configA = Configuration(spaceWidth = 100,
                              spaceHeight = 100,
                              numberAgents = 1000,
                              numberBehaviours = 2,
                              obstacleSide = 5, 
                              threshold = 10.0,
                              distanceInfluence = Influence.Linear,
                              netConstruction = Networker.Distance, 
                              transmission = Transmission.Air,
                              maxMove = 1.5,
                              obstacleTopLefts = obstaclesA,
                              exits = exitsA,
                              network = None)
  val agentsA = (1 to 1000).map(Agent.apply).toList
  val behavioursA = ((1 to 500).map(a => (Agent(a), Behaviour(0))) ++ (501 to 1000).map(a => (Agent(a), Behaviour(1)))).toMap
  val positionsA = (0 to 999).map(a => (Agent(a + 1), Position(a / 50 + 11, a % 50 + 11))).toMap
  val goalsA = (1 to 1000).map(a => (Agent(a), Position(0, 0))).toMap
  val recentSuccessesA = (1 to 1000).map(a => (Agent(a), 0.0)).toMap
  val stateA = State(config        = configA,
                     agents        = agentsA,
                     behaviour     = behavioursA,
                     position      = positionsA,
                     goal          = goalsA,
                     recentSuccess = recentSuccessesA)
  val behaviourCountsA = List(10, 20)
  val behaviourCountsB = List(1, 2, 99)