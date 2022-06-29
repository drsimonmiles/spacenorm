package study

import java.io.File
import spacenorm.SettingName
import study.Plot.*
import study.PlotData.*

@main def analyse(statsFolder: String): Unit = {
  // The folder for the plot images to be output to
  val plotsFolder = File(s"plots-${System.currentTimeMillis}")
  analyseFolder(File(statsFolder), plotsFolder)
}

def analyseFolder(statsFolder: File, plotsFolder: File): Unit = {
  statsFolder.listFiles.filter(_.isDirectory).foreach { subfolder =>
    analyseFolder(subfolder, File(plotsFolder, subfolder.getName))
  }
  if (statsFolder.listFiles.exists(!_.isDirectory))
    analyseStatsSet(statsFolder, plotsFolder)
}

def analyseStatsSet(statsFolder: File, plotsFolder: File): Unit = {
  // The collection of all results file in the given output data folder
  val collection: ResultsFolder = ResultsFolder(statsFolder)

  plotsFolder.mkdirs
  collection.comparisons.foreach { comparison =>
    exportTimeSeries(_.highestPrevalence, comparison, File(plotsFolder, s"prevalance-${comparison.suffix}.csv"))
    exportTimeSeries(_.diversity,         comparison, File(plotsFolder, s"diversity-${comparison.suffix}.csv"))
    exportTimeSeries(_.neighbourhood,     comparison, File(plotsFolder, s"neighbourhood-${comparison.suffix}.csv"))
    exportConvergenceChart(comparison, File(plotsFolder, s"converge-${comparison.suffix}.csv"))
    exportConvergenceTable(comparison, File(plotsFolder, s"converge-${comparison.suffix}.tex"))
  }
  collection.singletons.foreach { result =>
    exportTimeSeries(_.highestPrevalence, ResultsComparison(result), File(plotsFolder, s"prevalence-${result.prefix}.csv"))
    exportTimeSeries(_.diversity,         ResultsComparison(result), File(plotsFolder, s"diversity-${result.prefix}.csv"))
    exportTimeSeries(_.neighbourhood,     ResultsComparison(result), File(plotsFolder, s"neighbourhood-${result.prefix}.csv"))
  }
  
  println(s"Saved all plots to ${plotsFolder.getPath}")
}
/*
def plotStatsSet(statsFolder: File, plotsFolder: File): Unit = {
  // The collection of all results file in the given output data folder
  val collection: ResultsFolder = ResultsFolder(statsFolder)

  plotsFolder.mkdirs
  collection.systemComparisons.foreach { comparison =>
    plotTimeSeries("prevalence",    _.highestPrevalence, comparison, None, "Highest prevalence norm",   "systems", plotsFolder)
    plotTimeSeries("diversity",     _.diversity,         comparison, None, "Global norm diversity ",    "systems", plotsFolder)
    plotTimeSeries("neighbourhood", _.neighbourhood,     comparison, None, "Neighbourhood correlation", "systems", plotsFolder)
    plotConvergenceTimeBarChart  ("system", comparison, None, "Convergence time as system type varies",   plotsFolder)
    plotConvergenceChanceBarChart("system", comparison, None, "Chance to converge as system type varies", plotsFolder)
  }
  collection.settingComparisons.foreach {
    case (setting, comparisons) =>
      comparisons.foreach { comparison =>
        val prefix = setting.wildcardedPrefix(comparison.files.head.prefix, "varied")
        plotTimeSeries("prevalence",    _.highestPrevalence, comparison, Some(setting), "Highest prevalence norm",   prefix, plotsFolder)
        plotTimeSeries("diversity",     _.diversity,         comparison, Some(setting), "Global norm diversity ",    prefix, plotsFolder)
        plotTimeSeries("neighbourhood", _.neighbourhood,     comparison, Some(setting), "Neighbourhood correlation", prefix, plotsFolder)
        plotConvergenceTimeBarChart  (prefix, comparison, Some(setting), s"Convergence time as $setting varies",        plotsFolder)
        plotConvergenceChanceBarChart(prefix, comparison, Some(setting), s"Change to converge time as $setting varies", plotsFolder)
      }
  }
  collection.singletons.foreach { result =>
    plotTimeSeries("prevalence",    _.highestPrevalence, ResultsComparison(result), None, "Highest prevalence norm",   result.prefix, plotsFolder)
    plotTimeSeries("diversity",     _.diversity,         ResultsComparison(result), None, "Global norm diversity ",    result.prefix, plotsFolder)
    plotTimeSeries("neighbourhood", _.neighbourhood,     ResultsComparison(result), None, "Neighbourhood correlation", result.prefix, plotsFolder)
  }
  
  println(s"Saved all plots to ${plotsFolder.getPath}")
}*/