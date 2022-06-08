package sim

import sim.Metrics.{behaviourCounts, meanUtility, neighbourhoodCorrelation}
import spacenorm.State

/** A summary of key data about the state at the end of a given simulation tick. */
final case class TickResult(tick: Int, prevalences: List[Int], neighbourhood: Double, meanUtility: Double)

/** A summary of key data about the states across a simulation run. */
final case class Result(run: Int, ticks: List[TickResult]):
  def addTick(tick: Int, state: State): Result =
    copy(ticks = TickResult(tick, behaviourCounts(state), neighbourhoodCorrelation(state), meanUtility(state)) :: ticks)

object Result:
  def apply(run: Int, initialState: State): Result =
    Result(run, Nil).addTick(0, initialState)