package sim

import scala.util.Random
import spacenorm.*
import spacenorm.Agents.*
import spacenorm.Behaviour.randomBehaviour
import spacenorm.Configuration.configuration1
import spacenorm.Position.*

object Process:
  def newState: State = {
    val config    = configuration1
    val agents    = nextAgents(config.numberAgents)
    val behaviour = agents.map { agent => (agent, randomBehaviour(config)) }.toMap
    val position  = agents.map { agent => (agent, randomPosition(config)) }.toMap
    val goal      = agents.map { agent => (agent, randomPosition(config)) }.toMap
    val successes = agents.map { agent => (agent, 0.0) }.toMap

    State(config, agents, behaviour, position, goal, successes)
  }

  // 1. Each agent interacts with its neighbours
  def interact(state: State): State = {
    val thisRoundSuccesses = 
      state.agents.map{ agent =>
        val outcomes: List[Double] =
          state.neighbours(agent).toList.flatMap{ neighbour =>
            if (Random.nextDouble < state.influenceFactor(state.distanceBetween(agent, neighbour)))
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
    if (state.agents.size < state.config.numberAgents)
      agentsJoin(state.addAgent(
        nextAgent,
        randomBehaviour(state.config),
        state.config.randomExit, 
        randomPosition(state.config)
      ))
    else
      state