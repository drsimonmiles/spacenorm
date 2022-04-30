package spacenorm

import scala.util.Random

final case class Position(x: Int, y: Int)

object Position:
  def direction(from: Position, to: Position): Velocity = {
    val dx = if (from._1 == to._1) 0 else (from._1 - to._1) / (from._1 - to._1).abs
    val dy = if (from._2 == to._2) 0 else (from._2 - to._2) / (from._2 - to._2).abs
    Velocity (dx, dy)
  }

  def distance(position1: Position, position2: Position): Double =
    Math.sqrt (
      (position1._1 - position2._1) * (position1._1 - position2._1) +
      (position1._2 - position2._2) * (position1._2 - position2._2))

  def decodePosition(code: String): Option[Position] =
    (Decode.decodePair(Decode.decodeInt)(code)).map(xy => Position(xy._1, xy._2))

  def encodePosition(position: Position): String =
    Encode.encodePair(Encode.encodeInt)((position._1, position._2))

  def move(position: Position, dx: Int, dy: Int): Position =
    Position(position.x + dx, position.y + dy)

  def randomPosition(width: Int, height: Int): Position =
    Position(Random.nextInt(width), Random.nextInt(height))

  def randomPosition(configuration: Configuration): Position =
    randomPosition(configuration.spaceWidth, configuration.spaceHeight)

  def validPosition(position: Position, configuration: Configuration): Boolean =
    position._1 >= 0 && position._1 < configuration.spaceWidth && position._2 >= 0 && position._2 < configuration.spaceHeight