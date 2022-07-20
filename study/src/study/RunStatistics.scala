package study

import sim.Metrics.{convergenceRatio, highestPrevalence, diversity}
import sim.{Result, TickResult}
import sim.Result.tickRange

final case class TickStatistics(highestPrevalence: Double, diversity: Double, neighbourhood: Double)

final case class RunStatistics(ticks: List[TickStatistics], firstConverged: Option[Int])

final case class RunStatisticsSet(runs: List[RunStatistics]):
  lazy val numberTicks: Int = runs.head.ticks.size
  /** For each tick, list of tick stats across the runs */
  lazy val ticks: List[List[TickStatistics]] = 
    (0 until numberTicks).toList.map(tick => runs.map(_.ticks(tick)))
  /** For each tick, list of average stats */
  lazy val averages: List[TickStatistics] = aggregateAll(average)
  /** For each tick, list of standard deviations */
  lazy val stddevs: List[TickStatistics] = aggregateAll(stddev)
  /** For each tick, list of maximum stats */
  lazy val maximums: List[TickStatistics] = aggregateAll(_.maxOption)
  /** For each tick, list of minimum stats */
  lazy val minimums: List[TickStatistics] = aggregateAll(_.minOption)

  private def stddev(xs: List[Double]): Option[Double] = {
    average(xs).map { avg =>
      Math.sqrt(xs.map(a => math.pow(a - avg, 2)).sum / xs.size)
    }
  }
  private def average(values: List[Double]): Option[Double] =
    if (values.isEmpty) None else Some(values.sum / values.size)
  private def aggregateAll(f: List[Double] => Option[Double]): List[TickStatistics] =
    ticks.flatMap(stats => aggregate(stats, f))
  private def aggregate(tickStats: List[TickStatistics], f: List[Double] => Option[Double]): Option[TickStatistics] =
    for {
      highestPrevalence <- f(tickStats.map(_.highestPrevalence))
      diversity         <- f(tickStats.map(_.diversity))
      neighbourhood     <- f(tickStats.map(_.neighbourhood))
    } yield TickStatistics(highestPrevalence, diversity, neighbourhood)

object RunStatistics:
  def apply(ticks: List[TickStatistics]): RunStatistics =
    RunStatistics(ticks, ticks.zipWithIndex.find(_._1.highestPrevalence >= convergenceRatio).map(_._2))

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
    val convergence: Option[Int] =
      average(results.map(analyseRun).flatMap(_.firstConverged).map(_.toDouble)).map(_.toInt)
    RunStatistics(ticks, convergence)
  }