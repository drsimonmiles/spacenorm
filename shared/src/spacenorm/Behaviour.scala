package spacenorm

import scala.util.Random

final case class Behaviour(choice: Int) extends AnyVal

object Behaviour:
  def allBehaviours(configuration: Configuration): Set[Behaviour] =
    (0 until configuration.numberBehaviours).map(Behaviour.apply).toSet

  def randomBehaviour(configuration: Configuration): Behaviour =
    Behaviour(Random.nextInt(configuration.numberBehaviours))

  def decodeBehaviour(code: String): Option[Behaviour] =
    Decode.decodeInt(code).map(Behaviour.apply)

  def encodeBehaviour(behaviour: Behaviour): String =
    Encode.encodeInt(behaviour.choice)
