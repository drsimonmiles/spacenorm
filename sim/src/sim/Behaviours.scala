package sim

import scala.util.Random
import sim.Configuration.numberBehaviours

object Behaviours:
  opaque type Behaviour = Int

  val allBehaviours: Set[Behaviour] =
    (0 until numberBehaviours).toSet

  def randomBehaviour: Behaviour =
    Random.nextInt(numberBehaviours)
