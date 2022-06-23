package study

import spacenorm.Settings
import spacenorm.Influence
import spacenorm.Diffusion
import spacenorm.Networker
import spacenorm.Transmission
import org.jfree.chart.plot.Plot

enum PlottableSetting:
  case Agents, Behaviours, Obstacles, ObstacleSize, Exits, DistanceThreshold, LinearThreshold,
       InfluenceChoice, DiffusionChoice, NetworkChoice, TransmissionChoice, MaxMove

  def comparableGroups(results: List[ResultsFile]): List[List[ResultsFile]] =
    results.groupBy(result => ignoredIn(result.settings)).values.filter(_.size > 1).toList

  def extractFrom(settings: Settings): Double = this match {
    case Agents             => settings.numberAgents
    case Behaviours         => settings.numberBehaviours
    case Obstacles          => settings.numberObstacles
    case ObstacleSize       => settings.obstacleSide
    case Exits              => settings.numberExits
    case DistanceThreshold  => settings.distanceThreshold
    case LinearThreshold    => settings.linearThreshold
    case InfluenceChoice    => settings.distanceInfluence.ordinal
    case DiffusionChoice    => settings.diffusion.ordinal
    case NetworkChoice      => settings.netConstruction.ordinal
    case TransmissionChoice => settings.transmission.ordinal
    case MaxMove            => settings.maxMove
  }

  def ignoredIn(settings: Settings): Settings = this match {
    case Agents             => settings.copy(numberAgents = 0)
    case Behaviours         => settings.copy(numberBehaviours = 0)
    case Obstacles          => settings.copy(numberObstacles = 0)
    case ObstacleSize       => settings.copy(obstacleSide = 0)
    case Exits              => settings.copy(numberExits = 0)
    case DistanceThreshold  => settings.copy(distanceThreshold = 0.0)
    case LinearThreshold    => settings.copy(linearThreshold = 0.0)
    case InfluenceChoice    => settings.copy(distanceInfluence = Influence.Linear)
    case DiffusionChoice    => settings.copy(diffusion = Diffusion.Cascade)
    case NetworkChoice      => settings.copy(netConstruction = Networker.Distance)
    case TransmissionChoice => settings.copy(transmission = Transmission.Air)
    case MaxMove            => settings.copy(maxMove = 0.0)
  }

  def wildcardedPrefix(results: ResultsFile): String =
    results.prefix.split("-").updated(ordinal + 2, "*").mkString("-")

object PlottableSetting:
  def allComparableGroups(results: List[ResultsFile]): Map[PlottableSetting, List[List[ResultsFile]]] =
    PlottableSetting.values.map(setting => (setting, setting.comparableGroups(results))).toMap