package sim

import scala.util.Random
import sim.Configuration.numberBehaviours

object Behaviours:
  opaque type Behaviour = Int

  def randomBehaviour: Behaviour =
    Random.nextInt(numberBehaviours)
