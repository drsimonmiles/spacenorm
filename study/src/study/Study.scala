package study

import java.io.File
import org.jfree.chart.ChartFactory.createScatterPlot
import org.jfree.chart.ChartUtils.saveChartAsPNG
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import sim.Files.loadStats
import sim.Result
import study.RunStatistics.averageRuns
import org.jfree.chart.ChartRenderingInfo
import java.awt.geom.Rectangle2D
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.plot.PlotOrientation

@main def analyse(statsFile: String): Unit = {
  val results    = loadStats(File(statsFile))
  val stats      = averageRuns(results)

  plotTimeSeries("prevalence",    _.highestPrevalence, stats, "Highest prevalence norm")
  plotTimeSeries("diversity",     _.diversity,         stats, "Global norm diversity ")
  plotTimeSeries("neighbourhood", _.neighbourhood,     stats, "Neighbourhood correlation")
}

def plotTimeSeries(name: String, field: TickStatistics => Double, stats: RunStatistics, plotTitle: String): Unit = {
  val series     = XYSeries(name)
  val collection = XYSeriesCollection(series)
  //val render     = ChartRenderingInfo()

  stats.zipWithIndex.foreach { si =>
    series.add(si._2.toDouble, field(si._1))
  }
  val chart = createScatterPlot(plotTitle, "time", name, collection, PlotOrientation.VERTICAL, true, true, false)
  val plot  = chart.getPlot.asInstanceOf[XYPlot]
  val xAxis = plot.getDomainAxis.asInstanceOf[NumberAxis]
  xAxis.setRange(0, stats.size)
  xAxis.setTickUnit(new NumberTickUnit(0.1))
  xAxis.setVerticalTickLabels(true)
  val yAxis = plot.getRangeAxis.asInstanceOf[NumberAxis]
  yAxis.setRange(0.00, 1.00)
  yAxis.setTickUnit(new NumberTickUnit(0.1))
  yAxis.setVerticalTickLabels(true)

  //render.setChartArea(Rectangle2D.Double(0.0, 0.0, 1.0, 1.0))

  saveChartAsPNG(File(s"plots/$name.png"), chart, 1000, 1000)
  println(s"Saving to $name.png")
}