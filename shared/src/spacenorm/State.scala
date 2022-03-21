package spacenorm

import spacenorm.Agents.Agent
import spacenorm.Behaviours.Behaviour
import spacenorm.Positions.Position

type Goal = Position

final case class State(
  config: Instance,
  agents: List[Agent],
  edges: List[(Agent, Agent)],
  behaviour: Map[Agent, Behaviour],
  position: Map[Agent, Position],
  goal: Map[Agent, Goal],

  // Specific to coordination game diffusion
  recentSuccess: Map[Agent, Double]
):
  def addAgent(agent: Agent, agentBehaviour: Behaviour, agentPosition: Position, agentGoal: Goal): State =
    copy(agents = agent :: agents, behaviour = behaviour + (agent -> agentBehaviour),
         position = position + (agent -> agentPosition), goal = goal + (agent -> agentGoal))

  def neighbours(agent: Agent): Set[Agent] =
    edges.filter(_._1 == agent).map(_._2).toSet ++ edges.filter(_._2 == agent).map(_._1)

  def removeAgent(agent: Agent): State =
    copy(agents = agents.filter(_ != agent), edges = edges.filter(e => e._1 != agent && e._2 != agent),
         behaviour = behaviour.filter(_._1 != agent), position.filter(_._1 != agent), goal.filter(_._1 != agent))