package viz

import spacenorm.Position
import viz.Main.cellSize

object Projection:
  def project (coordinate: Position): Point =
    Point(coordinate.x * cellSize, coordinate.y * cellSize)

  def projectLine (line: (Position, Position)): (Point, Point) =
    (project (line._1), project (line._2))

  def unproject (point: Point): Position =
    Position(point.x.toInt / cellSize, point.y.toInt / cellSize)

  def unprojectLine (line: (Point, Point)): (Position, Position) =
    (unproject (line._1), unproject (line._2))
