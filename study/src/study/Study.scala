package study

import java.io.File
import org.jfree.chart.ChartFactory.createScatterPlot
import org.jfree.chart.ChartUtils.saveChartAsPNG
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import sim.Result
import spacenorm.SettingName
import study.PlottableSetting.{allComparableGroups, notInGroups}
import study.RunStatistics.{analyseRun, averageRuns}
import study.ResultsFile.{loadStatsCollection, lastTick}
import org.jfree.chart.ChartRenderingInfo
import java.awt.geom.Rectangle2D
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.plot.PlotOrientation

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

def plotTimeSeries(name: String, field: TickStatistics => Double, stats: List[ResultsFile],
                   setting: Option[SettingName], plotTitle: String, filePrefix: String): Unit = {
  val collection = XYSeriesCollection()
  stats.foreach { stat =>
    val series =
      setting.map { variable =>
        XYSeries(s"${variable.lowercase}=${variable.extractAsString(stat.settings)}")
      }.getOrElse(XYSeries(name))
    stat.averaged.ticks.zipWithIndex.foreach { si =>
      series.add(si._2.toDouble, field(si._1))
    }
    collection.addSeries(series)
  }
  
  val chart = createScatterPlot(plotTitle, "time", name, collection, PlotOrientation.VERTICAL, true, true, false)
  val plot  = chart.getPlot.asInstanceOf[XYPlot]
  val xAxis = plot.getDomainAxis.asInstanceOf[NumberAxis]
  xAxis.setRange(0, lastTick(stats))
  xAxis.setTickUnit(new NumberTickUnit(0.1))
  xAxis.setVerticalTickLabels(true)
  val yAxis = plot.getRangeAxis.asInstanceOf[NumberAxis]
  yAxis.setRange(0.00, 1.00)
  yAxis.setTickUnit(new NumberTickUnit(0.1))
  yAxis.setVerticalTickLabels(true)

  //render.setChartArea(Rectangle2D.Double(0.0, 0.0, 1.0, 1.0))

  File("plots").mkdir
  saveChartAsPNG(File(s"plots/$name-$filePrefix.png"), chart, 1000, 1000)
  println(s"Saving to $name-$filePrefix.png")
}

def plotConvergenceTime(name: String, stats: List[ResultsFile], parameter: SettingName, plotTitle: String): Unit = {
  val series     = XYSeries(name)
  val collection = XYSeriesCollection(series)

  stats.foreach { stat =>
    stat.averaged.firstConverged.foreach { convergence =>
      series.add(parameter.extractAsDouble(stat.settings), convergence.toDouble)
    }
  }
  val chart = createScatterPlot(plotTitle, parameter.toString, "time to converge", collection, PlotOrientation.VERTICAL, true, true, false)
  val plot  = chart.getPlot.asInstanceOf[XYPlot]
  val xAxis = plot.getDomainAxis.asInstanceOf[NumberAxis]
  //xAxis.setRange(0, stats.size)
  //xAxis.setTickUnit(new NumberTickUnit(0.1))
  //xAxis.setVerticalTickLabels(true)
  val yAxis = plot.getRangeAxis.asInstanceOf[NumberAxis]
  //yAxis.setRange(0.00, 1.00)
  //yAxis.setTickUnit(new NumberTickUnit(0.1))
  //yAxis.setVerticalTickLabels(true)

  //render.setChartArea(Rectangle2D.Double(0.0, 0.0, 1.0, 1.0))

  File("plots").mkdir
  saveChartAsPNG(File(s"plots/convergence-$name.png"), chart, 1000, 1000)
  println(s"Saving to convergence-$name.png")
}