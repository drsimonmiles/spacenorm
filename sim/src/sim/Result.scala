package sim

import sim.Metrics.{behaviourCounts, neighbourhoodCorrelation}
import spacenorm.State

final case class TickResult(tick: Int, prevalences: List[Int], neighbourhood: Double)

final case class Result(run: Int, ticks: List[TickResult]):
  def addTick(tick: Int, state: State): Result =
    copy(ticks = TickResult(tick, behaviourCounts(state), neighbourhoodCorrelation(state)) :: ticks)

object Result:
  def apply(run: Int): Result =
    Result(run, Nil)