package sim

import java.io.File
import scala.io.Source
import spacenorm.{Influence, Networker, Settings, Transmission}
import java.io.PrintWriter
import java.io.FileWriter

final class SettingException(message: String) extends Exception(message)

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
 
    def attribute[ItemType](name: String, convert: String => Option[ItemType] = s => Some(s)): ItemType =
      convert(attributes.get(name).getOrElse {
        throw new SettingException(s"Settings file ${file.getName} is missing $name attribute")
      }).getOrElse {
        throw new SettingException(s"Attribute $name in settings file ${file.getName} has the wrong type of value")
      }
    def enumValue[EnumType](valueOf: String => EnumType): String => Option[EnumType] = {
      name => try { Some(valueOf(name)) } catch { 
        case _: IllegalArgumentException => None          
      }
    }

    Settings(
      statsOutput       = attribute("statsOutput"),
      traceOutputPrefix = attribute("traceOutputPrefix"),
      numberRuns        = attribute("numberRuns", _.toIntOption),
      numberTraces      = attribute("numberTraces", _.toIntOption),
      numberTicks       = attribute("numberTicks", _.toIntOption),
      spaceWidth        = attribute("spaceWidth", _.toIntOption),
      spaceHeight       = attribute("spaceHeight", _.toIntOption),
      numberAgents      = attribute("numberAgents", _.toIntOption),
      numberBehaviours  = attribute("numberBehaviours", _.toIntOption),
      numberObstacles   = attribute("numberObstacles", _.toIntOption),
      obstacleSide      = attribute("obstacleSide", _.toIntOption),
      numberExits       = attribute("numberExits", _.toIntOption),
      threshold         = attribute("threshold", _.toDoubleOption),
      distanceInfluence = attribute("distanceInfluence", enumValue(Influence.valueOf)),
      netConstruction   = attribute("netConstruction", enumValue(Networker.valueOf)),
      transmission      = attribute("transmission", enumValue(Transmission.valueOf)),
      maxMove           = attribute("maxMove", _.toDoubleOption),
      randomSeed        = attribute("randomSeed", _.toLongOption)
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