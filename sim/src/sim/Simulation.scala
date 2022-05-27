package sim

import sim.Process.*
import spacenorm.Encode.{encodeConfiguration, encodeState}
import spacenorm.Behaviour.allBehaviours
import java.io.PrintWriter
import java.io.FileWriter
import java.io.File

@main def runExperiment = {
  val out = PrintWriter(FileWriter("experiment1.txt"))
  val config = spacenorm.Configuration.newFromSettings(File("config.toml"))
  val initial = newState(config)
  out.println(encodeConfiguration(config))

  val result = (1 to 10).foldLeft(initial) { (state, tick) =>
    println(tick)
    out.println(encodeState(state))
    val step1 = interact(state)
    val step2 = reviseBehaviour(step1)
    val step3 = moveAll(step2)
    val step4 = leave(step3)
    val step5 = chooseGoals(step4)
    agentsJoin(step5)
  }
  out.println(encodeState(result))
  out.close

  val finalBehaviours = result.behaviour.values
  for (behaviour <- allBehaviours(result.config))
    println(s"Number of agents with behaviour $behaviour: ${finalBehaviours.count(_ == behaviour)}")
}