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

  /**
   * Global diversity calculation, taken from:
   * Achieving Coordination in Multi-Agent Systems by Stable Local Conventions under Community Networks
   * Hu & Leung, 2007
   */
  def diversity(behaviourCounts: List[Int]): Double = {
    val total = behaviourCounts.sum.toDouble
    val frequencies = behaviourCounts.map(_ / total)         // p(a)
    def log2(x: Double): Double = log(x) /log(2)             // log_2
    val entropy = -frequencies.map { frequency =>            // H(a)
      if (frequency == 0.0) 0.0                              // Required as if frequency=0 for a behaviour
      else frequency * log2(frequency)                       // then log2(frequency) would be NaN
    }.sum

    entropy / log2(behaviourCounts.size)
  }

  def highestPrevalence(behaviourCounts: List[Int]): Double =
    behaviourCounts.max.toDouble / behaviourCounts.sum

/*  def meanUtility(state: State): Double = {
    val utilities = state.recentSuccess.values.toList
    utilities.sum / utilities.size
  }*/

  def neighbourhoodCorrelation(state: State): Double =
    val fractions = state.agents.flatMap { agent =>
      val neighbours = state.neighbours(agent)
      if (neighbours.size == 0)   // Exclude any agent with no neighbours from the calculation
        None
      else
        Some(neighbours.count(neighbour =>
          state.behaviour(agent) == state.behaviour(neighbour)
        ).toDouble / neighbours.size)
    }
    if (fractions.isEmpty) 0.0 else fractions.sum / fractions.size