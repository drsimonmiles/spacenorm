package sim

import java.io.File
import sim.Files.loadSettings
import sim.Process.runSimulation

/*
  The main method to run the simulation. The command line takes 4 arguments: the settings file to use to configure
  the simulation, the number of runs to perform, the number of traces to record to file
*/
@main def runExperiment(settingsFile: String) = {
  val settings = loadSettings(File(settingsFile))
  (1 to settings.numberRuns).foreach { run =>
    val traceFile =
      if (run <= settings.numberTraces)
        Some(File(s"${settings.traceOutputPrefix}$run.txt"))
      else
        None
    print(s"Run $run: ")
    runSimulation(settings, traceFile)
    println
  }

/*

  val finalBehaviours = result.behaviour.values
  for (behaviour <- allBehaviours(result.config))
    println(s"Number of agents with behaviour $behaviour: ${finalBehaviours.count(_ == behaviour)}")*/
}