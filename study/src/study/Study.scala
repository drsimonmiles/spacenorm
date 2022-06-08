package study

import java.io.File
import org.jfree.chart.ChartFactory.createScatterPlot
import org.jfree.chart.ChartUtils.saveChartAsPNG
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import sim.Files.loadStats
import sim.Result
import study.RunStatistics.averageRuns

@main def analyse(statsFile: String): Unit = {
  val results    = loadStats(File(statsFile))
  val stats      = averageRuns(results)

  plotTimeSeries("prevalence",    _.highestPrevalence, stats, "Highest prevalence norm")
  plotTimeSeries("utility",       _.meanUtility,       stats, "Average agent utility")
  plotTimeSeries("diversity",     _.diversity,         stats, "Global norm diversity ")
  plotTimeSeries("neighbourhood", _.neighbourhood,     stats, "Neighbourhood correlation")
}

def plotTimeSeries(name: String, field: TickStatistics => Double, stats: RunStatistics, plotTitle: String): Unit = {
  val series     = XYSeries(name)
  val collection = XYSeriesCollection(series)

  stats.zipWithIndex.foreach { si =>
    series.add(si._2.toDouble, field(si._1))
  }
  val chart = createScatterPlot(plotTitle, "time", name, collection)

  saveChartAsPNG(File(s"plots/$name.png"), chart, 1000, 1000)
  println(s"Saved to $name.png")
}