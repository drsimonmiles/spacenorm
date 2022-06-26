package spacenorm

enum SettingName:
  case SpaceWidth, SpaceHeight, NumberAgents, NumberBehaviours, NumberObstacles, ObstacleSide, NumberExits,
       DistanceThreshold, LinearThreshold, DistanceInfluence, Diffusion, NetConstruction, Transmission, MaxMove

  lazy val lowercase = toString.head.toLower + toString.tail

  def extractAsDouble(settings: Settings): Double = this match {
    case SpaceWidth        => settings.spaceWidth
    case SpaceHeight       => settings.spaceHeight
    case NumberAgents      => settings.numberAgents
    case NumberBehaviours  => settings.numberBehaviours
    case NumberObstacles   => settings.numberObstacles
    case ObstacleSide      => settings.obstacleSide
    case NumberExits       => settings.numberExits
    case DistanceThreshold => settings.distanceThreshold
    case LinearThreshold   => settings.linearThreshold
    case DistanceInfluence => settings.distanceInfluence.ordinal
    case Diffusion         => settings.diffusion.ordinal
    case NetConstruction   => settings.netConstruction.ordinal
    case Transmission      => settings.transmission.ordinal
    case MaxMove           => settings.maxMove
  }

  def extractAsString(settings: Settings): String = this match {
    case SpaceWidth        => settings.spaceWidth.toString
    case SpaceHeight       => settings.spaceHeight.toString
    case NumberAgents      => settings.numberAgents.toString
    case NumberBehaviours  => settings.numberBehaviours.toString
    case NumberObstacles   => settings.numberObstacles.toString
    case ObstacleSide      => settings.obstacleSide.toString
    case NumberExits       => settings.numberExits.toString
    case DistanceThreshold => settings.distanceThreshold.toString
    case LinearThreshold   => settings.linearThreshold.toString
    case DistanceInfluence => settings.distanceInfluence.toString
    case Diffusion         => settings.diffusion.toString
    case NetConstruction   => settings.netConstruction.toString
    case Transmission      => settings.transmission.toString
    case MaxMove           => settings.maxMove.toString
  }

  def ignoredIn(settings: Settings): Settings = this match {
    case SpaceWidth        => settings.copy(spaceWidth = 0)
    case SpaceHeight       => settings.copy(spaceHeight = 0)
    case NumberAgents      => settings.copy(numberAgents = 0)
    case NumberBehaviours  => settings.copy(numberBehaviours = 0)
    case NumberObstacles   => settings.copy(numberObstacles = 0)
    case ObstacleSide      => settings.copy(obstacleSide = 0)
    case NumberExits       => settings.copy(numberExits = 0)
    case DistanceThreshold => settings.copy(distanceThreshold = 0.0)
    case LinearThreshold   => settings.copy(linearThreshold = 0.0)
    case DistanceInfluence => settings.copy(distanceInfluence = Influence.Linear)
    case Diffusion         => settings.copy(diffusion = spacenorm.Diffusion.Cascade)
    case NetConstruction   => settings.copy(netConstruction = Networker.Distance)
    case Transmission      => settings.copy(transmission = spacenorm.Transmission.Air)
    case MaxMove           => settings.copy(maxMove = 0.0)
  }

  def wildcardedPrefix(prefix: String, wildcard: String): String =
    prefix.split("-").updated(ordinal, wildcard).mkString("-")