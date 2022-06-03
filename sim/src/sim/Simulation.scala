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
  /*val source = scala.io.Source.fromFile("experiment1.txt")
  val lines = source.getLines.take(5)
  val config = spacenorm.Decode.decodeConfiguration(lines.mkString("\n"))
  println(config.map(_.distanceInfluence.toString).getOrElse("none"))
  source.close
  System.exit(0)*/

  val settings = loadSettings(File(settingsFile))
  val results = (1 to settings.numberRuns).map(run => {
    print(s"Run $run: ")
    val traceFile =
      if (run <= settings.numberTraces)
        Some(File(s"${settings.traceOutputPrefix}$run.txt"))
      else
        None
    val result = runSimulation(run, settings, traceFile)
    println
    result
  }).toList

  saveStats(results, File(settings.statsOutput))
}