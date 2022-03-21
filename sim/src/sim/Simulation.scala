package sim

import sim.Process.*
import spacenorm.*

@main def runExperiment = {
  val initial = newState

  val result = (1 to 100).foldLeft(initial) { (state, tick) =>
    println(tick)
    val step1 = interact(state)
    val step2 = reviseBehaviour(step1)
    val step3 = moveAll(step2)
    val step4 = leave(step3)
    val step5 = chooseGoals(step4)
    val step6 = agentsJoin(step5)
    recalculateNetwork(step6)
  }

  val finalBehaviours = result.behaviour.values

  for (behaviour <- Behaviours.allBehaviours)
    println(s"Number of agents with behaviour $behaviour: ${finalBehaviours.count(_ == behaviour)}")
}