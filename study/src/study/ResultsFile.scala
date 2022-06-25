package study

import java.io.File
import sim.Files.{decodeFilename, loadStats}
import sim.Result
import spacenorm.Settings
import study.RunStatistics.{analyseRun, averageRuns}

final case class ResultsFile(file: File, settings: Settings, results: List[Result]):
  lazy val prefix: String            = file.getName.dropRight(4)
  lazy val averaged: RunStatistics   = averageRuns(results)
  lazy val fractionConverged: Double = results.map(analyseRun).count(_.firstConverged.nonEmpty) / results.size.toDouble

object ResultsFile:
  def apply(statsFile: File): ResultsFile =
    ResultsFile(statsFile, decodeFilename(statsFile), loadStats(statsFile))