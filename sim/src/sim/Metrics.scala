package sim

import spacenorm.State

object Metrics:
  val convergenceRatio = 0.95

  def behaviourCounts(state: State): Set[Int] =
    state.config.allBehaviours.map(behaviour => state.behaviour.values.count(_ == behaviour))

  def converged(state: State): Boolean =
    highestPrevalence(state) >= convergenceRatio

  def highestPrevalence(state: State): Double =
    behaviourCounts(state).max.toDouble / state.config.numberAgents