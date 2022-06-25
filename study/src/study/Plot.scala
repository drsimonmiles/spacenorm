package study

import java.io.File
import org.jfree.chart.ChartFactory.{createBarChart, createScatterPlot}
import org.jfree.chart.ChartUtils.saveChartAsPNG
import org.jfree.chart.axis.{NumberAxis, NumberTickUnit}
import org.jfree.chart.plot.{PlotOrientation, XYPlot}
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import spacenorm.SettingName
import study.SystemCategory.determineCategory
import org.jfree.data.category.DefaultCategoryDataset

object Plot:
  def plotTimeSeries(name: String, field: TickStatistics => Double, stats: ResultsComparison,
                     setting: Option[SettingName], plotTitle: String, filePrefix: String, plotsFolder: File): Unit = {
    val collection = XYSeriesCollection()
    stats.files.foreach { stat =>
      val series =
        setting.map { variable =>
          XYSeries(s"${variable.lowercase}=${variable.extractAsString(stat.settings)}")
        }.getOrElse(XYSeries(determineCategory(stat.settings).toString))
      stat.averaged.ticks.zipWithIndex.foreach { si =>
        series.add(si._2.toDouble, field(si._1))
      }
      collection.addSeries(series)
    }
  
    val chart = createScatterPlot(plotTitle, "time", name, collection, PlotOrientation.VERTICAL, true, true, false)
    val plot  = chart.getPlot.asInstanceOf[XYPlot]
    val xAxis = plot.getDomainAxis.asInstanceOf[NumberAxis]
    xAxis.setRange(0, stats.lastTick)
    xAxis.setTickUnit(new NumberTickUnit(0.1))
    xAxis.setVerticalTickLabels(true)
    val yAxis = plot.getRangeAxis.asInstanceOf[NumberAxis]
    yAxis.setRange(0.00, 1.00)
    yAxis.setTickUnit(new NumberTickUnit(0.1))
    yAxis.setVerticalTickLabels(true)

    //render.setChartArea(Rectangle2D.Double(0.0, 0.0, 1.0, 1.0))

    saveChartAsPNG(File(plotsFolder, s"$name-$filePrefix.png"), chart, 1000, 1000)
    println(s"Saving to $name-$filePrefix.png")
  }

  def plotConvergenceTime(name: String, stats: ResultsComparison, parameter: Option[SettingName], plotTitle: String, plotsFolder: File): Unit = {
    val series     = XYSeries(name)
    val collection = XYSeriesCollection(series)

    stats.files.foreach { stat =>
      stat.averaged.firstConverged.foreach { convergence =>
        parameter match {
          case Some(setting) =>
            series.add(setting.extractAsDouble(stat.settings), convergence.toDouble)
          case None =>
            series.add(determineCategory(stat.settings).ordinal, convergence.toDouble)
        }

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

    saveChartAsPNG(File(plotsFolder, s"convergence-$name.png"), chart, 1000, 1000)
    println(s"Saving to convergence-$name.png")
  }

  def plotConvergenceTimeBarChart(name: String, stats: ResultsComparison, parameter: Option[SettingName], plotTitle: String, plotsFolder: File): Unit = {
    val data = DefaultCategoryDataset()

    stats.files.sortBy { result =>
      parameter match {
        case Some(setting) => setting.extractAsDouble(result.settings)
        case None => determineCategory(result.settings).ordinal.toDouble
      }
    }.foreach { stat =>
      stat.averaged.firstConverged.foreach { convergence =>
        parameter match {
          case Some(setting) =>
            data.addValue(convergence, setting.extractAsString(stat.settings), "convergence time")
          case None =>
            data.addValue(convergence, determineCategory(stat.settings).toString, "convergence time")
        }

      }
    }
    val chart = createBarChart("Time to convergence", "System category", "", data)

    saveChartAsPNG(File(plotsFolder, s"convergence-$name.png"), chart, 1000, 1000)
    println(s"Saving to convergence-$name.png")
  }

  def plotConvergenceChanceBarChart(name: String, stats: ResultsComparison, parameter: Option[SettingName], plotTitle: String, plotsFolder: File): Unit = {
    val data = DefaultCategoryDataset()

    stats.files.sortBy { result =>
      parameter match {
        case Some(setting) => setting.extractAsDouble(result.settings)
        case None => determineCategory(result.settings).ordinal.toDouble
      }
    }.foreach { stat =>
      val fraction = stat.fractionConverged
      parameter match {
        case Some(setting) =>
          data.addValue(fraction, setting.extractAsString(stat.settings), "convergence time")
        case None =>
          data.addValue(fraction, determineCategory(stat.settings).toString, "convergence time")
      }
    }
    val chart = createBarChart("Proportion of runs converging", "System category", "", data)

    saveChartAsPNG(File(plotsFolder, s"fraction-$name.png"), chart, 1000, 1000)
    println(s"Saving to fraction-$name.png")
  }