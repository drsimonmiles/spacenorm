package study

import java.io.File
import sim.Files.{decodeFilename, loadStats}
import sim.Result
import spacenorm.Settings
import study.RunStatistics.averageRuns

final case class ResultsFile(file: File, settings: Settings, results: List[Result]):
  lazy val prefix: String          = file.getName.dropRight(4)
  lazy val averaged: RunStatistics = averageRuns(results)

object ResultsFile:
  def loadStatsCollection(statsFolder: File): List[ResultsFile] =
    statsFolder.listFiles.map { statsFile =>
      ResultsFile(statsFile, decodeFilename(statsFile), loadStats(statsFile))
    }.toList

  def lastTick(files: List[ResultsFile]): Int =
    files.flatMap(_.results).map(_.lastTick).max