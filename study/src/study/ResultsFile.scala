package study

import java.io.File
import sim.Files.{decodeFilename, loadStats}
import sim.Result
import spacenorm.Settings
import study.RunStatistics.{analyseRun, averageRuns}

final case class ResultsFile(file: File, results: List[Result]):
  lazy val settings: Settings         = decodeFilename(file)
  lazy val prefix: String             = file.getName.dropRight(4)
  lazy val averaged: RunStatistics    = averageRuns(results)
  lazy val analyses: RunStatisticsSet = RunStatisticsSet(results.map(analyseRun))
  lazy val numberConverged: Int       = analyses.runs.count(_.firstConverged.nonEmpty)
  lazy val fractionConverged: Double  = numberConverged / results.size.toDouble
  lazy val convergeTimes: List[Int]   = analyses.runs.flatMap(_.firstConverged)
  lazy val numberTicks: Int           = results.head.ticks.size

object ResultsFile:
  def loadResults(file: File): Option[ResultsFile] =
    loadStats(file).map(results => ResultsFile(file, results))