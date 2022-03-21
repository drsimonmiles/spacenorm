package sim

import scala.util.Random
import spacenorm.*
import spacenorm.Agents.*
import spacenorm.Behaviours.*
import spacenorm.Configuration.*
import spacenorm.Positions.*

object Process:
  def newState: State = {
    val config    = new Instance
    val agents    = nextAgents(numberAgents)
    val behaviour = agents.map { agent => (agent, randomBehaviour) }.toMap
    val position  = agents.map { agent => (agent, randomPosition) }.toMap
    val goal      = agents.map { agent => (agent, randomPosition) }.toMap
    val successes = agents.map { agent => (agent, 0.0) }.toMap

    recalculateNetwork(State(config, agents, Nil, behaviour, position, goal, successes))
  }

  // 1. Each agent interacts with its neighbours
  def interact(state: State): State = {
    val thisRoundSuccesses = 
      state.agents.map{ agent =>
        val outcomes: List[Double] =
          state.neighbours(agent).toList.flatMap{ neighbour =>
            if (Random.nextDouble < influenceFactor(distanceBetween(agent, neighbour, state)))
              if (state.behaviour(agent) == state.behaviour(neighbour))
                Some(1.0)
              else
                Some(0.0)
            else
              None
        }
        val successRate = outcomes.sum / outcomes.size
        (agent, successRate)
      }.toMap
    state.copy(recentSuccess = thisRoundSuccesses)
  }

  // 2. Revise behaviour
  def reviseBehaviour(state: State): State = {
    val newBehaviour = state.agents.map { agent =>
      // Find the neighbour who has recently been most successful
      val bestNeighbour: Agent = state.neighbours (agent).maxBy(neighbour => state.recentSuccess(neighbour))
      // Return the behaviour of that most successful neighbour
      (agent, state.behaviour(bestNeighbour))
    }.toMap
    state.copy(behaviour = newBehaviour)
  }

  // 3. Move
  def moveAll(state: State): State = {
    val newPositions =
      state.agents.map{ agent =>
        val velocity: Velocity = state.config.chooseVelocity(
          state.position(agent), state.goal(agent), state.behaviour(agent),
          state.position.values.toList, state.config.obstructed
        )
        val oldPosition = state.position(agent)
        val newPosition = velocity.moveFrom(oldPosition)
        (agent, newPosition)
      }.toMap
    state.copy(position = newPositions)
  }

    // 4. Leaving agents exit
  def leave(state: State): State =
    state.agents
      .filter(agent => state.config.exits.contains(state.position(agent)))
      .foldLeft(state) { (current, agent) =>
        current.removeAgent(agent)
      }

  // 5. Choose new goal
  def chooseGoals(state: State): State = {
    val newGoals =
      state.agents.map{ agent =>
        val newGoal = state.config.goalChoice(state.goal(agent), state.position(agent))
        (agent, newGoal)
      }.toMap
    state.copy(goal = newGoals)
  }

  // 6. New agents join
  def agentsJoin(state: State): State =
    if (state.agents.size < numberAgents)
      agentsJoin(state.addAgent(nextAgent, randomBehaviour, state.config.randomExit, randomPosition))
    else
      state      

  // 7. Recalculate network
  def recalculateNetwork(state: State): State = {
    val newEdges = state.agents.flatMap { agent1 =>
      state.agents.filter { agent2 => distanceBetween(agent1, agent2, state) <= threshold }
                  .map { agent2 => (agent1, agent2) }
    }
    state.copy(edges = newEdges)
  }
