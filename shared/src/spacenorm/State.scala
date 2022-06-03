package spacenorm

import spacenorm.Position.distance

type Goal = Position

/** The state of a simulation run at a given instant. */
final case class State(
  config: Configuration,
  agents: List[Agent],
  behaviour: Map[Agent, Behaviour],
  position: Map[Agent, Position],
  goal: Map[Agent, Goal],
  network: Option[Map[Agent, List[Agent]]],
  recentSuccess: Map[Agent, Double]
):
  lazy val neighbours: Map[Agent, List[Agent]] =
    network.getOrElse {
      agents.map { agent1 =>
        (agent1, agents.filter { agent2 => agent2 != agent1 && distanceBetween(agent1, agent2) <= config.threshold })
      }.toMap
    }

  def addAgent(agent: Agent, agentBehaviour: Behaviour, agentPosition: Position, agentGoal: Goal): State =
    copy(
      agents = agent :: agents,
      behaviour = behaviour + (agent -> agentBehaviour),
      position = position + (agent -> agentPosition),
      goal = goal + (agent -> agentGoal),
      recentSuccess = recentSuccess + (agent -> 0.0)
    )

  def distanceBetween(agent1: Agent, agent2: Agent): Double =
    distance(position(agent1), position(agent2))

  def removeAgent(agent: Agent): State =
    copy(
      agents = agents.filter(_ != agent),
      behaviour = behaviour.filter(_._1 != agent),
      position = position.filter(_._1 != agent),
      goal = goal.filter(_._1 != agent),
      recentSuccess = recentSuccess.filter(_._1 != agent)
    )

  def setPosition(agent: Agent, newPosition: Position): State =
    copy(position = position.filter(_._1 != agent) + (agent -> newPosition))