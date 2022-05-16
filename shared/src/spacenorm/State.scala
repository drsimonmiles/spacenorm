package spacenorm

import spacenorm.Agents.Agent
import spacenorm.Behaviour
import spacenorm.Position.*

type Goal = Position

final case class State(
  config: Configuration,
  agents: List[Agent],
  behaviour: Map[Agent, Behaviour],
  position: Map[Agent, Position],
  goal: Map[Agent, Goal],

  // Specific to coordination game diffusion
  recentSuccess: Map[Agent, Double]
):
  lazy val edges = 
    agents.flatMap { agent1 =>
      agents.filter { agent2 => distanceBetween(agent1, agent2) <= config.threshold }.map { agent2 => (agent1, agent2) }
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

  def influenceFactor(distance: Double): Double =
    config.influenceFactor(distance)

  def neighbours(agent: Agent): Set[Agent] =
    edges.filter(_._1 == agent).map(_._2).toSet ++ edges.filter(_._2 == agent).map(_._1)

  def removeAgent(agent: Agent): State =
    copy(
      agents = agents.filter(_ != agent),
      behaviour = behaviour.filter(_._1 != agent),
      position = position.filter(_._1 != agent),
      goal = goal.filter(_._1 != agent),
      recentSuccess = recentSuccess.filter(_._1 != agent)
    )
