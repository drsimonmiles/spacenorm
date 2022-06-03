package spacenorm

enum Influence:
  case Linear, Uniform

  /** The influence factor at a given distance. */
  def influenceFactor(distance: Double, threshold: Double): Double =
    this match {
      case Linear  => 1 - distance / threshold
      case Uniform => 1.0
    }