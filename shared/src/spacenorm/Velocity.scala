package spacenorm

import spacenorm.Positions.*

case class Velocity(dx: Int, dy: Int):
  def moveFrom(from: Position): Position =
    move(from, dx, dy)

  /** Rotate the velocity clockwise */
  def rotate: Velocity =
    Velocity(
      if (dx == 0) -dy else if (dx ==  dy) 0 else dx,
      if (dy == 0)  dx else if (dx == -dy) 0 else dy
    )

  /** A list of this rotation plus its 7 rotations in clockwise order */
  lazy val rotations: List[Velocity] =
    someRotations(8, Nil)

  private def someRotations(number: Int, found: List[Velocity]): List[Velocity] =
    if (number <= 0) found.reverse else rotate.someRotations(number - 1, this :: found)
