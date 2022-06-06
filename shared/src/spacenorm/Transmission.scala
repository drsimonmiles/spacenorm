package spacenorm

import Position.{distance, lineJoining}

enum Transmission:
  case Light, Air

  def accessible(from: Position, to: Position, threshold: Double, obstructed: Position => Boolean): Boolean = 
    this match {
      case Air =>
        distance(from, to) <= threshold
      case Light =>
        distance(from, to) <= threshold && !lineJoining(from, to).pointsAlong(from).exists(obstructed)
    }