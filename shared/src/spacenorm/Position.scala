package spacenorm

import scala.util.Random

final case class Position(x: Int, y: Int)

object Position:
  def direction(from: Position, to: Position): Velocity = {
    val dx = if (from.x == to.x) 0 else (to.x - from.x) / (to.x - from.x).abs
    val dy = if (from.y == to.y) 0 else (to.y - from.y) / (to.y - from.y).abs
    Velocity (dx, dy)
  }

  def distance(position1: Position, position2: Position): Double =
    Math.sqrt (
      (position1.x - position2.x) * (position1.x - position2.x) +
      (position1.y - position2.y) * (position1.y - position2.y))

  def decodePosition(code: String): Option[Position] =
    (Decode.decodePair(Decode.decodeInt)(code)).map(xy => Position(xy._1, xy._2))

  def encodePosition(position: Position): String =
    Encode.encodePair(Encode.encodeInt)((position.x, position.y))

  def move(position: Position, dx: Int, dy: Int): Position =
    Position(position.x + dx, position.y + dy)

  def randomPosition(width: Int, height: Int): Position =
    Position(Random.nextInt(width), Random.nextInt(height))

  def randomValidPosition(configuration: Configuration): Position = {
    val position = randomPosition(configuration.spaceWidth, configuration.spaceHeight)
    if (validAgentPosition(position, configuration))
      position
    else
      randomValidPosition(configuration)
  }

  def validAgentPosition(position: Position, configuration: Configuration): Boolean =
    position.x >= 0 && position.x < configuration.spaceWidth &&
      position.y >= 0 && position.y < configuration.spaceHeight &&
      !configuration.obstructed.contains(position)