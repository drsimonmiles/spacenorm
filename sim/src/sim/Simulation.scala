package sim

import sim.Process.*
import spacenorm.Encode.{encodeConfiguration, encodeState}
import spacenorm.Behaviours
import java.io.PrintWriter
import java.io.FileWriter

@main def runExperiment = {
  val out = PrintWriter(FileWriter("experiment1.txt"))

  val initial = newState
  out.println(encodeConfiguration(initial.config))

  val result = (1 to 100).foldLeft(initial) { (state, tick) =>
    println(tick)
    out.println(encodeState(state))
    val step1 = interact(state)
    val step2 = reviseBehaviour(step1)
    val step3 = moveAll(step2)
    val step4 = leave(step3)
    val step5 = chooseGoals(step4)
    val step6 = agentsJoin(step5)
    recalculateNetwork(step6)
  }
  out.println(encodeState(result))
  out.close

  val finalBehaviours = result.behaviour.values
  for (behaviour <- Behaviours.allBehaviours(result.config))
    println(s"Number of agents with behaviour $behaviour: ${finalBehaviours.count(_ == behaviour)}")
}