package spacenorm

final case class Position(x: Int, y: Int):
  def move(dx: Int, dy: Int): Position =
    Position(x + dx, y + dy)

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
