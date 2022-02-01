package sim

import scala.util.Random
import sim.Configuration._

object Positions:
  opaque type Position = (Int, Int)

  def at(x: Int, y: Int): Position =
    (x, y)

  def direction(from: Position, to: Position): Velocity = {
    val dx = if (from._1 == to._1) 0 else (from._1 - to._1) / (from._1 - to._1).abs
    val dy = if (from._2 == to._2) 0 else (from._2 - to._2) / (from._2 - to._2).abs
    Velocity (dx, dy)
  }

  def distance(position1: Position, position2: Position): Double =
    Math.sqrt (
      (position1._1 - position2._1) * (position1._1 - position2._1) +
      (position1._2 - position2._2) * (position1._2 - position2._2))

  def move(position: Position, dx: Int, dy: Int): Position =
    (position._1 + dx, position._2 + dy)

  def randomPosition: Position =
    (Random.nextInt(spaceWidth), Random.nextInt(spaceHeight))

  def validPosition(position: Position): Boolean =
    position._1 >= 0 && position._1 < spaceWidth && position._2 >= 0 && position._2 < spaceHeight