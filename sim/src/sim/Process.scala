package sim

import java.io.{File, FileWriter, PrintWriter}
import scala.util.Random
import sim.Generate.*
import spacenorm.{Agent, Position, Settings, State, Velocity}
import spacenorm.Encode.{encodeConfiguration, encodeState}
import spacenorm.Position.direction

/** Implements the process for enacting a single run of a simulation. */
object Process:
  def runSimulation(run: Int, settings: Settings, traceFile: Option[File]): Result = {
    val initialState = newState(newRunConfiguration(settings))
    val trace = traceFile.map(file => PrintWriter(FileWriter(file)))

    trace.foreach(_.println(encodeConfiguration(initialState.config)))
    val (finalState, result) =
      (1 to settings.numberTicks).foldLeft((initialState, Result(run, initialState))) { 
        case ((state, result), tick) =>
          print(".")
          trace.foreach(_.println(encodeState(state)))
          val step1 = interact(state)
          val step2 = reviseBehaviour(step1)
          val step3 = moveAll(step2)
          val step4 = leave(step3)
          val step5 = chooseGoals(step4)
          val nextState = agentsJoin(step5)
          (nextState, result.addTick(tick, nextState))
        }
    trace.foreach(_.println(encodeState(finalState)))
    trace.foreach(_.close)

    result
  }

  // 1. Each agent interacts with its neighbours
  def interact(state: State): State = {
    val thisRoundSuccesses = 
      state.agents.map{ agent =>
        val outcomes: List[Double] =
          state.neighbours(agent).toList.flatMap{ neighbour =>
            if (Random.nextDouble < state.config.influenceFactor(state.distanceBetween(agent, neighbour)))
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
    // Note we need each agent to be acting on the updated state to avoid two moving to the same position
    val agents: List[Agent]       = state.agents
    var positions: List[Position] = agents.map(state.position)
    val newPositions = state.agents.zipWithIndex.map { (agent, index) =>
      val velocity: Velocity = 
        direction(state.position(agent), state.goal(agent)).rotations.find { velocity =>
          var moved = velocity.moveFrom(state.position(agent))
          state.config.validAgentPosition(moved) && !positions.contains(moved)
        }.getOrElse(Velocity(0, 0))
      val oldPosition = state.position(agent)
      val newPosition = velocity.moveFrom(oldPosition)
      positions = positions.updated(index, newPosition)
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
        val newGoal = chooseGoal(state.goal(agent), state.position(agent), state.config)
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
        randomExit(state.config),
        randomValidPosition(state.config)
      ))
    else
      state
