package sim

import java.io.File
import scala.io.Source
import spacenorm.{Influence, Networker, Settings, Transmission}
import java.io.PrintWriter
import java.io.FileWriter

/** Functionality to load and save simulation-related data, such as configuration settings and stats output. */
object Files:
  def loadTOML(file: File): Map[String, String] = {
    val source = Source.fromFile(file)
    val lines = source.getLines.toList
    source.close
    lines
      .map(_.takeWhile(_ != '#'))         // Remove comments
      .filter(_.contains("="))
      .map(_.trim)
      .map(_.split("="))
      .map { parts =>
        val key   = parts(0).trim
        val value = parts(1).trim.replaceAll("^\"|\"$", "")    // Remove surrounding quotes from value if any
        (key, value)
    }.toMap
  }

  def loadSettings(file: File): Settings = {
    val attributes: Map[String, String] = loadTOML(file)
 
    Settings(
      statsOutput       = attributes("statsOutput"),
      traceOutputPrefix = attributes("traceOutputPrefix"),
      numberRuns        = attributes("numberRuns").toInt,
      numberTraces      = attributes("numberTraces").toInt,
      numberTicks       = attributes("numberTicks").toInt,
      spaceWidth        = attributes("spaceWidth").toInt,
      spaceHeight       = attributes("spaceHeight").toInt,
      numberAgents      = attributes("numberAgents").toInt,
      numberBehaviours  = attributes("numberBehaviours").toInt,
      numberObstacles   = attributes("numberObstacles").toInt,
      obstacleSide      = attributes("obstacleSide").toInt,
      numberExits       = attributes("numberExits").toInt,
      threshold         = attributes("threshold").toDouble,
      distanceInfluence = Influence.valueOf(attributes("distanceInfluence")),
      netConstruction   = Networker.valueOf(attributes("netConstruction")),
      transmission      = Transmission.valueOf(attributes("transmission")),
      maxMove           = attributes("maxMove").toDouble,
      randomSeed        = attributes("randomSeed").toLong
    )
  }

  def saveStats(results: List[Result], statsFile: File): Unit = {
    val runOffset: Int = 
      if (statsFile.exists) {
        val source = Source.fromFile(statsFile)
        val maxRun = source.getLines.map {
          line =>
            if (line.contains(","))
              line.split(",").head.toInt
            else
              0
        }.max
        source.close
        maxRun
      } else 0

    val out = PrintWriter(FileWriter(statsFile, true))
    results.foreach { result =>
      result.ticks.foreach { tick =>
        out.println(s"${result.run + runOffset},${tick.tick},${tick.prevalences.mkString(";")},${tick.neighbourhood},${tick.meanUtility}")
      }
    }
    out.close
  }

  def loadStats(statsFile: File): List[Result] = {
    val source = Source.fromFile(statsFile)
    val loaded =
      source.getLines.filter(_.contains(",")).foldLeft[List[Result]](Nil) {
        (results, line) =>
          val fields = line.split(",")
          val run  = fields(0).toInt
          val tick = fields(1).toInt
          val prevalences = fields(2).split(";").map(_.toInt).toList
          val neighbourhood = fields(3).toDouble
          val meanUtility = fields(4).toDouble
          val tickResult = TickResult(tick, prevalences, neighbourhood, meanUtility)
          val runResult = results.find(_.run == run).getOrElse(Result(run)).addTick(tickResult)
          runResult :: results.filterNot(_.run == run)
      }
    source.close

    loaded
  }