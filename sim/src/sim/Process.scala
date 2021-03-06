package sim

import scala.util.Random
import sim.Generate.{chooseGoal, nextAgent, randomBehaviour, randomExit, randomValidPosition}
import spacenorm.{Agent, Networker, Position, Settings, State, Velocity}
import spacenorm.Position.direction

/** Implements the model-specific process for enacting a single run of a simulation. */
object Process:
  def runTick(state: State, previous: Option[State], random: Random): State = {
    val step1and2 = interactAndDiffuse(state, previous, random)
    if (state.config.netConstruction == Networker.Distance && state.config.maxMove > 0.0) {
      val step3 = moveAll(step1and2)
      val step4 = leave(step3)
      val step5 = chooseGoals(step4, random)
      agentsJoin(step5, random)
    } else step1and2
  }

  // 1. Each agent interacts with its neighbours 
  // OBSOLETE, now incorporated into interactAndDiffuse
  /*
  def interactAll(state: State, random: Random): State = {
    val thisRoundSuccesses = 
      state.agents.map{ agent =>
        val outcomes: List[Double] =
          state.neighbours(agent).toList.flatMap{ neighbour =>
            if (random.nextDouble < state.config.influenceFactor(state.distanceBetween(agent, neighbour)))
              if (state.behaviour(agent) == state.behaviour(neighbour))
                Some(1.0)
              else
                Some(-1.0)
            else
              None
        }
        val successRate = if (outcomes.size == 0) 0.0 else outcomes.sum / outcomes.size
        (agent, successRate)
      }.toMap
    state.copy(recentSuccess = thisRoundSuccesses)
  }*/

  // 2. Revise behaviour
  // OBSOLETE, now incorporated into interactAndDiffuse
  /*
  def reviseBehaviour(state: State): State = {
    val newBehaviour = state.agents.map { agent =>
      // Find the neighbour who has recently been most successful, or itself if it has no neighbours
      val bestNeighbour: Agent = state.neighbours(agent).maxByOption(neighbour => state.recentSuccess(neighbour)).getOrElse(agent)
      // Return the behaviour of that most successful neighbour
      (agent, state.behaviour(bestNeighbour))
    }.toMap
    state.copy(behaviour = newBehaviour)
  }*/

  // 1 & 2. Interact and diffuse
  def interactAndDiffuse(state: State, previous: Option[State], random: Random): State = {
    import state.config.diffusion

    // Calculate the link-indepdenent power of each agent on others to copy its behaviour
    val linkIndependentPower = state.agents.map { agent => (agent, diffusion.ownPower(agent, state, previous, random)) }.toMap
    val newBehaviour = state.agents.map { agent =>
      // Calculate the link-dependent pressure of each neighbour on the agent to copy its behaviour
      val agentPressure = state.neighbours(agent).map { neighbour =>
        (neighbour, diffusion.linkPower(neighbour, agent, state, random) * linkIndependentPower(neighbour))
      }
      // Calculate the pressure per each behaviour on the agent to adopt that behaviour
      val behaviourPressure = state.config.allBehaviours.map { behaviour =>
        (behaviour, agentPressure.filter(p => state.behaviour(p._1) == behaviour).map(_._2).sum)
      }
      // Filter the behaviours to only those with sufficient pressure
      val filteredPressure = behaviourPressure.filter(_._2 >= diffusion.threshold(state))
      // Select the new behaviour as the one with highest pressure, or keep the current behaviour if none are pressuring
      (agent, filteredPressure.maxByOption(_._2).map(_._1).getOrElse(state.behaviour(agent)))
    }.toMap

    state.copy(behaviour = newBehaviour)
  }

  // 3. Move
  def moveAll(state: State): State = {
    // Note we need each agent to be acting on the running updated state to avoid two moving to the same position
    val newPositions = 
      state.agents.foldLeft(state.position) { (positions, agent) =>
        val newPosition =
          direction(positions(agent), state.goal(agent))         // Most direct direction from agent to its goal
            .rotations                                           // All possible rotations clockwise from most direct
            .filter(_.distance <= state.config.maxMove)          // Only allow movement up to maximum distance in settings
            .map(_.moveFrom(state.position(agent)))              // Position where agent would end if moving each direction
            .filter(state.config.validAgentPosition)             // Exclude positions outside space or on obstacle
            .filterNot(pos => positions.values.exists(_ == pos)) // Exclude positions containing other agents
            .headOption                                          // Get the first direction still valid, if any
            .getOrElse(state.position(agent))                    // If no direction is valid, agent stays where it is
        positions + (agent -> newPosition)
      }
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
  def chooseGoals(state: State, random: Random): State = {
    val newGoals =
      state.agents.map{ agent =>
        val newGoal = chooseGoal(state.goal(agent), state.position(agent), state.config, random)
        (agent, newGoal)
      }.toMap
    state.copy(goal = newGoals)
  }

  // 6. New agents join
  def agentsJoin(state: State, random: Random): State =
    if (state.agents.size < state.config.numberAgents)
      agentsJoin(state.addAgent(
        nextAgent,
        randomBehaviour(state.config, random),
        randomExit(state.config, random),
        randomValidPosition(state.config, random)
      ), random)
    else
      state
