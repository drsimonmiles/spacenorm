package study

import sim.Metrics.{convergenceRatio, highestPrevalence, diversity}
import sim.{Result, TickResult}
import sim.Result.tickRange

final case class TickStatistics(highestPrevalence: Double, diversity: Double, neighbourhood: Double)

final case class RunStatistics(ticks: List[TickStatistics], firstConverged: Option[Double])

object RunStatistics:
  def apply(ticks: List[TickStatistics]): RunStatistics =
    RunStatistics(ticks, ticks.zipWithIndex.find(_._1.highestPrevalence >= convergenceRatio).map(_._2.toDouble))

  def analyseTick(tickResult: TickResult): TickStatistics =
    TickStatistics(
      highestPrevalence = highestPrevalence(tickResult.prevalences),
      diversity         = diversity(tickResult.prevalences),
      neighbourhood     = tickResult.neighbourhood
    )

  def analyseRun(result: Result): RunStatistics =
    RunStatistics(result.getOrderedTicks.map(analyseTick))

  def average(values: List[Double]): Option[Double] =
    if (values.isEmpty) None else Some(values.sum / values.size)

  def averageStats(tickStatistics: List[TickStatistics]): Option[TickStatistics] =
    for {
      highestPrevalence <- average(tickStatistics.map(_.highestPrevalence))
      diversity         <- average(tickStatistics.map(_.diversity))
      neighbourhood     <- average(tickStatistics.map(_.neighbourhood))
    } yield TickStatistics(highestPrevalence, diversity, neighbourhood)

  def averageRuns(results: List[Result]): RunStatistics = {
    val ticks =
      tickRange(results).flatMap { tick =>
        averageStats(results.flatMap(_.getTick(tick)).map(analyseTick))
      }.toList
    println(s"${results.map(analyseRun).flatMap(_.firstConverged).size}")
    val convergence = average(results.map(analyseRun).flatMap(_.firstConverged))
    RunStatistics(ticks, convergence)
  }