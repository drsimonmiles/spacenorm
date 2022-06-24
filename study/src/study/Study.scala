package study

import java.io.File
import spacenorm.SettingName
import study.PlottableSetting.{allComparableGroups, notInGroups}
import study.ResultsFile.loadStatsCollection
import study.Plot.{plotTimeSeries, plotConvergenceTime}

@main def analyse(statsFolder: String, arguments: String*): Unit = {
  // The collection of all results file in the given output data folder
  val collection: List[ResultsFile] = loadStatsCollection(File(statsFolder))
  // If we give the -system argument, the plots will compare results by system category rather than parameter settings
  val systemGrouping = arguments.contains("-system")
  // The folder for the plot images to be output to
  val plotsFolder = File(s"plots-${System.currentTimeMillis}")

  plotsFolder.mkdir
  if (systemGrouping) {
    plotTimeSeries("prevalence",    _.highestPrevalence, collection, None, "Highest prevalence norm",   "systems", plotsFolder)
    plotTimeSeries("diversity",     _.diversity,         collection, None, "Global norm diversity ",    "systems", plotsFolder)
    plotTimeSeries("neighbourhood", _.neighbourhood,     collection, None, "Neighbourhood correlation", "systems", plotsFolder)
    plotConvergenceTime("system", collection, None, "Convergence time as system type varies", plotsFolder)
  } else {
    // The groups of results giving a comparison across values of a setting
    val groups: Map[SettingName, List[List[ResultsFile]]] = allComparableGroups(collection)
    // The results not in any comparative group
    val singles: List[ResultsFile] = notInGroups(collection, groups)

    groups.foreach {
      case (setting, comparisons) =>
        comparisons.foreach { comparison =>
          val prefix = setting.wildcardedPrefix(comparison.head.prefix)
          plotTimeSeries("prevalence",    _.highestPrevalence, comparison, Some(setting), "Highest prevalence norm",   prefix, plotsFolder)
          plotTimeSeries("diversity",     _.diversity,         comparison, Some(setting), "Global norm diversity ",    prefix, plotsFolder)
          plotTimeSeries("neighbourhood", _.neighbourhood,     comparison, Some(setting), "Neighbourhood correlation", prefix, plotsFolder)
          plotConvergenceTime(prefix, comparison, Some(setting), s"Convergence time as $setting varies", plotsFolder)
        }
    }
    singles.foreach { result =>
      plotTimeSeries("prevalence",    _.highestPrevalence, List(result), None, "Highest prevalence norm",   result.prefix, plotsFolder)
      plotTimeSeries("diversity",     _.diversity,         List(result), None, "Global norm diversity ",    result.prefix, plotsFolder)
      plotTimeSeries("neighbourhood", _.neighbourhood,     List(result), None, "Neighbourhood correlation", result.prefix, plotsFolder)
    }
  }
  println(s"Saved all plots to ${plotsFolder.getName}")
}