package study

import java.io.File
import org.jfree.chart.ChartFactory.createScatterPlot
import org.jfree.chart.ChartUtils.saveChartAsPNG
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import sim.Result
import spacenorm.SettingName
import study.PlottableSetting.allComparableGroups
import study.RunStatistics.{analyseRun, averageRuns}
import study.ResultsFile.loadStatsCollection
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

  for (file <- collection) {
    val averaged = averageRuns(file.results)
    plotTimeSeries("prevalence",    _.highestPrevalence, averaged, "Highest prevalence norm",   file.prefix)
    plotTimeSeries("diversity",     _.diversity,         averaged, "Global norm diversity ",    file.prefix)
    plotTimeSeries("neighbourhood", _.neighbourhood,     averaged, "Neighbourhood correlation", file.prefix)
  }
  groups.foreach {
    case (setting, comparisons) =>
      comparisons.foreach { comparison =>
        plotEmergenceTime(setting.wildcardedPrefix(comparison.head.prefix), comparison, setting, s"Convergence time as $setting varies")
      }
  }
}

def plotTimeSeries(name: String, field: TickStatistics => Double, stats: RunStatistics, plotTitle: String, filePrefix: String): Unit = {
  val series     = XYSeries(name)
  val collection = XYSeriesCollection(series)
  //val render     = ChartRenderingInfo()

  stats.ticks.zipWithIndex.foreach { si =>
    series.add(si._2.toDouble, field(si._1))
  }
  val chart = createScatterPlot(plotTitle, "time", name, collection, PlotOrientation.VERTICAL, true, true, false)
  val plot  = chart.getPlot.asInstanceOf[XYPlot]
  val xAxis = plot.getDomainAxis.asInstanceOf[NumberAxis]
  xAxis.setRange(0, stats.ticks.size)
  xAxis.setTickUnit(new NumberTickUnit(0.1))
  xAxis.setVerticalTickLabels(true)
  val yAxis = plot.getRangeAxis.asInstanceOf[NumberAxis]
  yAxis.setRange(0.00, 1.00)
  yAxis.setTickUnit(new NumberTickUnit(0.1))
  yAxis.setVerticalTickLabels(true)

  //render.setChartArea(Rectangle2D.Double(0.0, 0.0, 1.0, 1.0))

  saveChartAsPNG(File(s"plots/$filePrefix-$name.png"), chart, 1000, 1000)
  println(s"Saving to $filePrefix-$name.png")
}

def plotEmergenceTime(name: String, stats: List[ResultsFile], parameter: SettingName, plotTitle: String): Unit = {
  val series     = XYSeries(name)
  val collection = XYSeriesCollection(series)

  stats.foreach { stat =>
    stat.averaged.firstConverged.foreach { convergence =>
      series.add(parameter.extractFrom(stat.settings), convergence.toDouble)
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

  saveChartAsPNG(File(s"plots/$name.png"), chart, 1000, 1000)
  println(s"Saving to $name.png")
}