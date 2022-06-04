package spacenorm

import Position.{distance, lineJoining}

enum Transmission:
  case Light, Air

  def accessible(width: Int, height: Int, threshold: Double, obstructed: Position => Boolean): (Position, Position) => Boolean = 
    this match {
      case Air =>
        (from, to) => distance(from, to) <= threshold
      case Light =>
        (from, to) => distance(from, to) <= threshold && !lineJoining(from, to).pointsAlong(from).exists(obstructed)
    }