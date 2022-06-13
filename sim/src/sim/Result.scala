package sim

import sim.Metrics.{behaviourCounts, meanUtility, neighbourhoodCorrelation}
import spacenorm.State

/** A summary of key data about the state at the end of a given simulation tick. */
final case class TickResult(tick: Int, prevalences: List[Int], neighbourhood: Double, meanUtility: Double)

/** A summary of key data about the states across a simulation run. */
final case class Result(run: Int, ticks: List[TickResult]):
  def addTick(tickResult: TickResult): Result =
    copy(ticks = tickResult :: ticks)

  def addTick(tick: Int, state: State): Result =
    addTick(TickResult(tick, behaviourCounts(state), neighbourhoodCorrelation(state), meanUtility(state)))

  def getTick(tick: Int): Option[TickResult] =
    ticks.find(_.tick == tick)

  def getOrderedTicks: List[TickResult] =
    tickRange.flatMap(getTick).toList

  lazy val lastTick: Int = ticks.map(_.tick).maxOption.getOrElse(-1)
  lazy val tickRange: Range = 0 to lastTick

object Result:
  def apply(run: Int): Result =
    Result(run, Nil)

  def apply(run: Int, initialState: State): Result =
    Result(run).addTick(0, initialState)

  def tickRange(results: List[Result]): Range =
    0 to results.map(_.lastTick).max