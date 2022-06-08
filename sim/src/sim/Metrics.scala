package sim

import java.lang.Math.log
import spacenorm.State

/** Functionality to analyse simulation states to determine key aggregate metrics. */
object Metrics:
  val convergenceRatio = 0.95

  def behaviourCounts(state: State): List[Int] =
    state.config.allBehaviours.map(behaviour => state.behaviour.values.count(_ == behaviour))

  def converged(behaviourCounts: List[Int]): Boolean =
    highestPrevalence(behaviourCounts) >= convergenceRatio

  def diversity(behaviourCounts: List[Int]): Double = {
    val total = behaviourCounts.sum.toDouble
    val frequencies = behaviourCounts.map(_ / total)                              // p(a)
    def log2(x: Double): Double = log(x) /log(2)                                  // log_2
    val entropy = -frequencies.map(frequency => frequency * log2(frequency)).sum  // H(a)
    
    entropy / log2(behaviourCounts.size)
  }

  def highestPrevalence(behaviourCounts: List[Int]): Double =
    behaviourCounts.max.toDouble / behaviourCounts.sum

  def meanUtility(state: State): Double = {
    val utilities = state.recentSuccess.values.toList
    utilities.sum / utilities.size
  }

  def neighbourhoodCorrelation(state: State): Double =
    val fractions = state.agents.map { agent =>
      val neighbours = state.neighbours(agent)
      neighbours.count(neighbour => state.behaviour(agent) == state.behaviour(neighbour)).toDouble / neighbours.size
    }
    fractions.sum / fractions.size