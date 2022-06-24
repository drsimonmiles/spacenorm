package study

import java.io.File
import org.jfree.chart.ChartFactory.createScatterPlot
import org.jfree.chart.ChartUtils.saveChartAsPNG
import org.jfree.chart.axis.{NumberAxis, NumberTickUnit}
import org.jfree.chart.plot.{PlotOrientation, XYPlot}
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import spacenorm.SettingName
import study.ResultsFile.lastTick

object Plot:
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