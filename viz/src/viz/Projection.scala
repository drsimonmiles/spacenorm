package viz

import spacenorm.Position

object Projection:
  def project (coordinate: Position): Point =
    Point(coordinate.x, coordinate.y)

  def projectLine (line: (Position, Position)): (Point, Point) =
    (project (line._1), project (line._2))

  def unproject (point: Point): Position =
    Position(point.x.toInt, point.y.toInt)

  def unprojectLine (line: (Point, Point)): (Position, Position) =
    (unproject (line._1), unproject (line._2))
