package spacenorm

/** Denotes how the network of agents is constructed and its dynamics. */
enum Networker:
  case Distance, Random

  def presentableName: String =
    this match {
      case Distance => "Distance-based network"
      case Random => "Random network"
    }