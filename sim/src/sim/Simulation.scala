package sim

import java.io.File
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import sim.Files.{loadSettings, saveStats}
import sim.Process.runSimulation

/*
  The main method to run the simulation. The command line takes an argument: the settings file.
*/
@main def runExperiment(settingsFile: String) = {
  val settings = loadSettings(File(settingsFile))
  val results = (1 to settings.numberRuns).map(run => {
    val traceFile =
      if (run <= settings.numberTraces)
        Some(File(s"${settings.traceOutputPrefix}$run.txt"))
      else
        None
    runSimulation(run, settings, traceFile)
  }).toList

  saveStats(results, File(settings.statsOutput))
}