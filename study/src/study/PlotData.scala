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

  def exportRangedSeries(kind: PlotKind, stats: ResultsFile, dataFolder: File): Unit = {
    val file = File(dataFolder, kind.dataFileName(stats))
    val out = PrintWriter(FileWriter(file))
    val averages = stats.analyses.averages.map(kind.extract)
    val stddevs = stats.analyses.stddevs.map(kind.extract)
    out.println(s"time,average,min,max")
    for (tick <- 0 until stats.numberTicks) {
      out.print(s"$tick,")
      out.print(averages(tick).toString)
      out.print(",")
      out.println(stddevs(tick).toString)
    }
    out.close
  }

  def exportPlotHeader(out: PrintWriter): Unit = {
    out.println("set term png size 600, 400")
    out.println("set datafile separator \",\"")
    out.println("set grid")
    out.println("set autoscale")
    out.println("set xlabel \"Time\"")
    out.println
  }

  def exportPlotScript(stats: ResultsComparison, plotFolder: File, kind: PlotKind, out: PrintWriter): Unit = {
    out.println(s"set output \"${kind.plotFile(plotFolder, stats).getAbsolutePath}\"")
    out.println(s"set yrange ${kind.yRange}")
    out.println(s"set ylabel \"${kind.yLabel}\"")
    val commands = stats.files
      .map(results => (kind.dataFileName(results), seriesName(results.settings, stats.comparator)))
      .map{ case (file, title) => s"\"$file\" skip 1 using 1:2 title \"$title\" with lines" }
      .mkString(",")
    out.println(s"plot $commands")
    out.println
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

/*  def exportRawData(stat: TickStatistics => Double, file: ResultsFile, outFile: File): Unit = {
    val out = PrintWriter(FileWriter(outFile))
    out.print("Time,")
    out.println(file.analyses.indices.map(r => s"Run $r").mkString(","))
    for (tick <- 0 until file.numberTicks) {
      out.print(s"$tick,")
      out.println(file.analyses.map { analysis => stat(analysis.ticks(tick)) }.map(x => f"$x%1.2f").mkString(","))
    }
    out.close
  }*/