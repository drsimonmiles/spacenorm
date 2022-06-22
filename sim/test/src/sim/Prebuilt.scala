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
                              distanceThreshold = 10.0,
                              linearThreshold = 3.0,
                              distanceInfluence = Influence.Linear,
                              diffusion = Diffusion.Coordination,
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
  val stateA = State(config            = configA,
                     agents            = agentsA,
                     behaviour         = behavioursA,
                     position          = positionsA,
                     goal              = goalsA)
  val agentsB = (1 to 3).map(Agent.apply).toList
  val behavioursB = Map(Agent(1) -> Behaviour(0), Agent(2) -> Behaviour(1), Agent(3) -> Behaviour(0))
  val positionsB = Map(Agent(1) -> Position(20, 20), Agent(2) -> Position(21, 21), Agent(3) -> Position(22, 22))
  val goalsB = Map(Agent(1) -> Position(21, 21), Agent(2) -> Position(22, 22), Agent(3) -> Position(20, 20))
  val recentSuccessesB = Map(Agent(1) -> 0.2, Agent(2) -> 0.3, Agent(3) -> 0.4)
  val stateB = State(config            = configA,
                     agents            = agentsB,
                     behaviour         = behavioursB,
                     position          = positionsB,
                     goal              = goalsB)
  val behaviourCountsA = List(10, 20)
  val behaviourCountsB = List(1, 2, 99)