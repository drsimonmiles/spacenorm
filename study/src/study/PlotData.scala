package study

import java.io.{File, FileWriter, PrintWriter}
import spacenorm.SettingName
import study.SystemCategory.determineCategory
import spacenorm.Settings
import spacenorm.SettingName.*

object PlotData:
  def seriesName(settings: Settings, comparator: Option[SettingName]): String =
    comparator.map { variable => variable match {
      case DistanceInfluence | Diffusion | NetConstruction | Transmission =>
        variable.extractAsString(settings)
      case _ =>
        s"${variable.lowercase}=${variable.extractAsString(settings)}"
    }}.getOrElse(determineCategory(settings).presentableName)

  def exportTimeSeries(field: TickStatistics => Double, stats: ResultsComparison, file: File): Unit = {
    val out = PrintWriter(FileWriter(file))
    val series: List[(String, List[Double])] =
      stats.orderedFiles.map { stat =>
        val name = seriesName(stat.settings, stats.comparator)
        val data = stat.averaged.ticks.map(field)
        (name, data)
      }
    out.println(s"time,${series.map(_._1).mkString(",")}")
    for (tick <- series.head._2.indices) {
      out.print(s"$tick,")
      out.println(series.map(_._2(tick).toString).mkString(","))
    }
    out.close
  }

  val tickBlockSize = 10

  def exportConvergenceChart(stats: ResultsComparison, file: File): Unit = {
    val out = PrintWriter(FileWriter(file))
    val blocks: List[(Int, Int)] =
      (0 to stats.lastTick by tickBlockSize).map(t => (t, t + tickBlockSize - 1)).toList
    val categories: List[String] = "No convergence" :: blocks.map(block => s"${block._1} to ${block._2}")
    val series: List[(String, List[Double])] =
      stats.orderedFiles.map { stat =>
        val name = seriesName(stat.settings, stats.comparator)
        val unconverged = 1.0 - stat.fractionConverged
        val data = blocks.map { block =>
          stat.convergeTimes.count(t => t >= block._1 && t <= block._2) / stat.numberConverged.toDouble
        }
        (name, unconverged :: data)
      }
    out.println(s"time,${series.map(_._1).mkString(",")}")
    for (block <- blocks.indices) {
      out.print(s"${categories(block)},")
      out.println(series.map(_._2(block).toString).mkString(","))
    }
    out.close
  }

  def exportConvergenceTable(stats: ResultsComparison, file: File): Unit = {
    val out = PrintWriter(FileWriter(file))
    out.println("& Runs where norm emerged & First time norm emerged (avg.)\\\\ \\hline")
    stats.orderedFiles.map { stat => 
      out.print(s"${seriesName(stat.settings, stats.comparator)} & ")
      out.print(s"${(stat.fractionConverged * 100).toInt}\\% & ")
      stat.averaged.firstConverged.foreach(t => out.print(t.toString))
      out.println(" \\\\")
    }
    out.close
  }