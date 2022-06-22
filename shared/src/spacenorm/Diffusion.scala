package spacenorm

import scala.util.Random

enum Diffusion:
  case Coordination, Threshold, Cascade

  def ownPower(agent: Agent, state: State, previous: Option[State], random: Random): Double =
    this match {
      case Coordination =>
        val outcomes: List[Double] =
          state.neighbours(agent).toList.flatMap{ neighbour =>
            if (random.nextDouble <= state.influence(agent, neighbour))
              if (state.behaviour(agent) == state.behaviour(neighbour))
                Some(1.0)
              else
                Some(-1.0)
            else
              None
        }
        if (outcomes.size == 0) 0.0 else outcomes.sum / outcomes.size
      case Threshold => 1.0
      case Cascade =>
        if (previous.flatMap(_.behaviour.get(agent)).exists(_ == state.behaviour(agent))) 0.0 else 1.0
    }

  def linkPower(fromAgent: Agent, toAgent: Agent, state: State, random: Random): Double =
    this match {
      case Coordination => 1.0
      case Threshold => state.influence(fromAgent, toAgent)
      case Cascade => if (random.nextDouble <= state.influence(fromAgent, toAgent)) 1.0 else 0.0
    }

  def threshold(state: State): Double = 
    this match {
      case Coordination => 0.0
      case Threshold => state.config.linearThreshold
      case Cascade => 0.0
    }