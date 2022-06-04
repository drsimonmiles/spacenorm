package spacenorm

final case class Velocity(dx: Int, dy: Int):
  lazy val distance: Double =
    Math.sqrt((dx * dx) + (dy * dy))

  def moveFrom(from: Position): Position =
    from.move(dx, dy)

  def pointsAlong(from: Position): Set[Position] = {
    val maxLength   = dx.abs.max(dy.abs)
    val dxPerLength = dx.toDouble / maxLength
    val dyPerLength = dy.toDouble / maxLength

    (0 to maxLength).map { step => 
      val x = Math.round(from.x + dxPerLength * step).toInt
      val y = Math.round(from.y + dyPerLength * step).toInt
      Position(x, y)
    }.toSet
  }

  def unitStep: Velocity =
    Velocity (if (dx == 0) 0 else dx / dx.abs, if (dy == 0) 0 else dy / dy.abs)

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
