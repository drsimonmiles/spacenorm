package sim

import java.io.File
import scala.io.Source
import spacenorm.*
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

  def loadSettings(file: File): SettingsSpace = {
    val attributes: Map[String, String] = loadTOML(file)
 
    def convertOrComplain[ItemType](name: String, value: String, convert: String => Option[ItemType] = s => Some(s)): ItemType =
      convert(value).getOrElse {
        throw new SettingException(s"Attribute $name in settings file ${file.getName} has an invalid value")
      }
    def attributeOption[ItemType](name: String, convert: String => Option[ItemType] = s => Some(s)): Option[ItemType] =
      attributes.get(name).map(convertOrComplain(name, _, convert))
    def attribute[ItemType](name: String, convert: String => Option[ItemType] = s => Some(s)): ItemType =
      attributeOption(name, convert).getOrElse {
        throw new SettingException(s"Settings file ${file.getName} is missing $name attribute")
      }
    def enumValue[EnumType](valueOf: String => EnumType): String => Option[EnumType] =
      name => try { Some(valueOf(name.capitalize)) } catch { 
        case _: IllegalArgumentException => None          
      }
    def listValue(value: String): Option[List[String]] = {
      if (value.head == '[' && value.last == ']')
        Some(value.drop(1).dropRight(1).split(",").map(_.trim).toList)
      else
        None
    }
    def varyValues(setting: SettingName, base: Settings, values: List[String]): VariedSettings = {
      def toInt(value: String) = 
        convertOrComplain(setting.lowercase, value, _.toIntOption)
      def toDouble(value: String) = 
        convertOrComplain(setting.lowercase, value, _.toDoubleOption)
      def toEnum[EnumType](value: String, valueOf: String => EnumType) = 
        convertOrComplain(setting.lowercase, value, enumValue(valueOf))
      val space = values.map { value =>
        setting match {
          case SettingName.SpaceWidth        => base.copy(spaceWidth        = toInt(value))
          case SettingName.SpaceHeight       => base.copy(spaceHeight       = toInt(value))
          case SettingName.NumberAgents      => base.copy(numberAgents      = toInt(value))
          case SettingName.NumberBehaviours  => base.copy(numberBehaviours  = toInt(value))
          case SettingName.NumberObstacles   => base.copy(numberObstacles   = toInt(value))
          case SettingName.ObstacleSide      => base.copy(obstacleSide      = toInt(value))
          case SettingName.NumberExits       => base.copy(numberExits       = toInt(value))
          case SettingName.DistanceThreshold => base.copy(distanceThreshold = toDouble(value))
          case SettingName.LinearThreshold   => base.copy(linearThreshold   = toDouble(value))
          case SettingName.DistanceInfluence => base.copy(distanceInfluence = toEnum(value, Influence.valueOf))
          case SettingName.Diffusion         => base.copy(diffusion         = toEnum(value, Diffusion.valueOf))
          case SettingName.NetConstruction   => base.copy(netConstruction   = toEnum(value, Networker.valueOf))
          case SettingName.Transmission      => base.copy(transmission      = toEnum(value, Transmission.valueOf))
          case SettingName.MaxMove           => base.copy(maxMove           = toDouble(value))
        }
      }
      VariedSettings(setting, space)
  }

    val base = Settings(
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
      linearThreshold   = attribute("linearThreshold", _.toDoubleOption),
      distanceThreshold = attribute("distanceThreshold", _.toDoubleOption),
      distanceInfluence = attribute("distanceInfluence", enumValue(Influence.valueOf)),
      diffusion         = attribute("diffusion", enumValue(Diffusion.valueOf)),
      netConstruction   = attribute("netConstruction", enumValue(Networker.valueOf)),
      transmission      = attribute("transmission", enumValue(Transmission.valueOf)),
      maxMove           = attribute("maxMove", _.toDoubleOption),
      randomSeed        = attribute("randomSeed", _.toLongOption)
    )
    attributeOption("vary", enumValue(SettingName.valueOf)).map { setting =>
      varyValues(setting, base, attribute("values", listValue))
    }.getOrElse(SingleSettings(base))
  }

  def statsFilePrefix(settings: Settings): String = {
    import settings.*
    s"$spaceWidth-$spaceHeight-$numberAgents-$numberBehaviours-$numberObstacles-$obstacleSide-$numberExits-$distanceThreshold-$linearThreshold-$distanceInfluence-$diffusion-$netConstruction-$transmission-$maxMove"
  }

  def decodeFilename(statsFile: File): Settings = {
    val fields = statsFile.getName.dropRight(4).split("-")
    Settings(
      statsOutput       = statsFile.getParent,
      traceOutputPrefix = "",
      numberRuns        = 0,
      numberTraces      = 0,
      numberTicks       = 0,
      spaceWidth        = fields(0).toInt,
      spaceHeight       = fields(1).toInt,
      numberAgents      = fields(2).toInt,
      numberBehaviours  = fields(3).toInt,
      numberObstacles   = fields(4).toInt,
      obstacleSide      = fields(5).toInt,
      numberExits       = fields(6).toInt,
      distanceThreshold = fields(7).toDouble,
      linearThreshold   = fields(8).toDouble,
      distanceInfluence = Influence.valueOf(fields(9)),
      diffusion         = Diffusion.valueOf(fields(10)),
      netConstruction   = Networker.valueOf(fields(11)),
      transmission      = Transmission.valueOf(fields(12)),
      maxMove           = fields(13).toDouble,
      randomSeed        = -1
    )
  }

  def saveStats(result: Result, statsFile: File): Unit = {
    val uniqueRunID: Int = 
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
        maxRun + 1
      } else 1

    val out = PrintWriter(FileWriter(statsFile, true))
    result.ticks.reverse.foreach { tick =>
      out.println(s"$uniqueRunID,${tick.tick},${tick.prevalences.mkString(";")},${tick.neighbourhood}")
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
          val tickResult = TickResult(tick, prevalences, neighbourhood)
          val runResult = results.find(_.run == run).getOrElse(Result(run)).addTick(tickResult)
          runResult :: results.filterNot(_.run == run)
      }
    source.close

    loaded
  }