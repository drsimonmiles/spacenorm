package spacenorm

/** A position in the grid space in which agents can move and interact. */
final case class Position(x: Int, y: Int):
  def move(dx: Int, dy: Int): Position =
    Position(x + dx, y + dy)

object Position:
  def direction(from: Position, to: Position): Velocity =
    lineJoining(from, to).unitStep

  def distance(position1: Position, position2: Position): Double =
    Math.sqrt (
      (position1.x - position2.x) * (position1.x - position2.x) +
      (position1.y - position2.y) * (position1.y - position2.y))

  def lineJoining(from: Position, to: Position): Velocity = {
    val dx = to.x - from.x
    val dy = to.y - from.y
    Velocity(dx, dy)
  }