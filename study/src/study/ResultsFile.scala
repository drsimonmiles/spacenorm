package study

import java.io.File
import sim.Files.{decodeFilename, loadStats}
import sim.Result
import spacenorm.Settings
import study.RunStatistics.{analyseRun, averageRuns}

final case class ResultsFile(file: File):
  lazy val settings: Settings            = decodeFilename(file)
  lazy val results: List[Result]         = loadStats(file)
  lazy val prefix: String                = file.getName.dropRight(4)
  lazy val averaged: RunStatistics       = averageRuns(results)
  lazy val analyses: List[RunStatistics] = results.map(analyseRun)
  lazy val numberConverged: Int          = analyses.count(_.firstConverged.nonEmpty)
  lazy val fractionConverged: Double     = numberConverged / results.size.toDouble
  lazy val convergeTimes: List[Int]      = analyses.flatMap(_.firstConverged)