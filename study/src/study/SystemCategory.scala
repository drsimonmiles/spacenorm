package study

import spacenorm.{Influence, Networker, Settings}
import spacenorm.SettingName.{NetConstruction, DistanceInfluence, MaxMove, NumberObstacles, ObstacleSide, NumberExits}

enum SystemCategory:
  case Base, SpatiallyDistributed, Distanced, Movement, Obstructed, Permeable

object SystemCategory:
  val systemRelevantSettings = Set(NetConstruction, DistanceInfluence, MaxMove, NumberObstacles, ObstacleSide, NumberExits)

  def determineCategory(settings: Settings): SystemCategory =
    if (settings.netConstruction == Networker.Random) Base
    else if (settings.distanceInfluence == Influence.Uniform) SpatiallyDistributed
    else if (settings.maxMove <= 0.0) Distanced
    else if (settings.numberObstacles == 0) Movement
    else if (settings.numberExits == 0) Obstructed
    else Permeable

  def ignoreSystemDistinguishers(settings: Settings): Settings =
    systemRelevantSettings.foldLeft(settings) {
      (current, setting) => setting.ignoredIn(current)
    }