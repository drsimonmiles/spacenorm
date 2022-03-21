package spacenorm

import scala.util.Random
import spacenorm.Configuration.numberBehaviours

object Behaviours:
  opaque type Behaviour = Int

  val allBehaviours: Set[Behaviour] =
    (0 until numberBehaviours).toSet

  def randomBehaviour: Behaviour =
    Random.nextInt(numberBehaviours)
