package study

import spacenorm.{Influence, Networker, Settings}

object SystemCategory:
  def determineCategory(settings: Settings): String =
    if (settings.netConstruction == Networker.Random) "base"
    else if (settings.distanceInfluence == Influence.Uniform) "spatially distributed"
    else if (settings.maxMove <= 0.0) "distanced"
    else if (settings.numberObstacles == 0) "movement"
    else if (settings.numberExits == 0) "obstructed"
    else "permeable"

  def determineCategoryCode(settings: Settings): Double =
    if (settings.netConstruction == Networker.Random) 0
    else if (settings.distanceInfluence == Influence.Uniform) 1
    else if (settings.maxMove <= 0.0) 2
    else if (settings.numberObstacles == 0) 3
    else if (settings.numberExits == 0) 4
    else 5