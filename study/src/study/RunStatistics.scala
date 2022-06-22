package study

import sim.Metrics.{highestPrevalence, diversity}
import sim.{Result, TickResult}
import sim.Result.tickRange

final case class TickStatistics(highestPrevalence: Double, diversity: Double, neighbourhood: Double)

type RunStatistics = List[TickStatistics]

object RunStatistics:
  def analyseTick(tickResult: TickResult): TickStatistics =
    TickStatistics(
      highestPrevalence = highestPrevalence(tickResult.prevalences),
      diversity         = diversity(tickResult.prevalences),
      neighbourhood     = tickResult.neighbourhood
    )

  def analyseRun(result: Result): RunStatistics = 
    result.getOrderedTicks.map(analyseTick)

  def average(values: List[Double]): Double =
    values.sum / values.size

  def averageStats(tickStatistics: List[TickStatistics]): TickStatistics =
    TickStatistics(
      highestPrevalence = average(tickStatistics.map(_.highestPrevalence)),
      diversity         = average(tickStatistics.map(_.diversity)),
      neighbourhood     = average(tickStatistics.map(_.neighbourhood))
    )

  def averageRuns(results: List[Result]): RunStatistics =
    tickRange(results).map { tick =>
      averageStats(results.flatMap(_.getTick(tick)).map(analyseTick))
    }.toList