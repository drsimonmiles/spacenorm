package sim

import java.io.File
import scala.io.Source
import spacenorm.Settings

object Files:
  def loadTOML(file: File): Map[String, String] = {
    val source = Source.fromFile(file)
    val lines = source.getLines.toList
    source.close
    lines.map(_.trim).filter(_.contains("=")).map(_.split("=")).map { parts =>
        val key   = parts(0).trim
        val value = parts(1).trim.replaceAll("^\"|\"$", "")
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
      maxMove           = attributes("maxMove").toDouble
    )
  }
