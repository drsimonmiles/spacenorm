package study

import java.io.File
import spacenorm.SettingName
import study.PlottableSetting.{allComparableGroups, notInGroups}
import study.ResultsFile.loadStatsCollection
import study.Plot.{plotTimeSeries, plotConvergenceTime}

@main def analyse(statsFolder: String): Unit = {
  val folder: File = File(statsFolder)
  val collection: List[ResultsFile] = loadStatsCollection(folder)
  val groups: Map[SettingName, List[List[ResultsFile]]] = allComparableGroups(collection)
  val singles: List[ResultsFile] = notInGroups(collection, groups)

  /*for (file <- collection) {
    val averaged = averageRuns(file.results)
    plotTimeSeries("prevalence",    _.highestPrevalence, averaged, "Highest prevalence norm",   file.prefix)
    plotTimeSeries("diversity",     _.diversity,         averaged, "Global norm diversity ",    file.prefix)
    plotTimeSeries("neighbourhood", _.neighbourhood,     averaged, "Neighbourhood correlation", file.prefix)
  }*/
  groups.foreach {
    case (setting, comparisons) =>
      comparisons.foreach { comparison =>
        val prefix = setting.wildcardedPrefix(comparison.head.prefix)
        plotTimeSeries("prevalence",    _.highestPrevalence, comparison, Some(setting), "Highest prevalence norm",   prefix)
        plotTimeSeries("diversity",     _.diversity,         comparison, Some(setting), "Global norm diversity ",    prefix)
        plotTimeSeries("neighbourhood", _.neighbourhood,     comparison, Some(setting), "Neighbourhood correlation", prefix)
        plotConvergenceTime(prefix, comparison, setting, s"Convergence time as $setting varies")
      }
  }
  singles.foreach { result =>
    plotTimeSeries("prevalence",    _.highestPrevalence, List(result), None, "Highest prevalence norm",   result.prefix)
    plotTimeSeries("diversity",     _.diversity,         List(result), None, "Global norm diversity ",    result.prefix)
    plotTimeSeries("neighbourhood", _.neighbourhood,     List(result), None, "Neighbourhood correlation", result.prefix)
  }
}