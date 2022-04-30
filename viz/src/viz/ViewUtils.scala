package viz

import org.scalajs.dom.MouseEvent
import org.scalajs.dom.html.Canvas

object ViewUtils {
  def distance (point1: Point, point2: Point): Double =
    Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y))

  def distanceToLine (point: Point, end1: Point, end2: Point): Double =
    ((end2.x - end1.x) * (end1.y - point.y) - (end1.x - point.x) * (end2.y - end1.y)).abs / distance (end1, end2)

  def mousePoint (event: MouseEvent, canvas: Canvas): Point =
    Point (event.clientX - canvas.getBoundingClientRect.left, event.clientY -  canvas.getBoundingClientRect.top)

  def nearestLine (point: Point, lines: Vector[(Point, Point)]): (Point, Point) =
    lines.map (line => (line, distanceToLine (point, line._1, line._2))).minBy (_._2)._1

  def withinCircle (point: Point, centre: Point, radius: Double): Boolean =
    distance (point, centre) <= radius

  def withinRectangle (point: Point, topLeft: Point, size: Size): Boolean =
    point.x >= topLeft.x && point.y >= topLeft.y && point.x < topLeft.x + size.width && point.y < topLeft.y + size.height
}