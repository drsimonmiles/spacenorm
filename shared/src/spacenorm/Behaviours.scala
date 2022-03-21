package spacenorm

import scala.util.Random

object Behaviours:
  opaque type Behaviour = Int

  def allBehaviours(configuration: Configuration): Set[Behaviour] =
    (0 until configuration.numberBehaviours).toSet

  def randomBehaviour(configuration: Configuration): Behaviour =
    Random.nextInt(configuration.numberBehaviours)

  def decodeBehaviour(code: String): Option[Behaviour] =
    Decode.decodeInt(code)

  def encodeBehaviour(behaviour: Behaviour): String =
    Encode.encodeInt(behaviour)
