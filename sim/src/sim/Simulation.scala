package sim

import java.io.File
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import sim.Files.loadSettings
import sim.Process.runSimulation

/*
  The main method to run the simulation. The command line takes 4 arguments: the settings file to use to configure
  the simulation, the number of runs to perform, the number of traces to record to file
*/
@main def runExperiment(settingsFile: String) = {
  val settings = loadSettings(File(settingsFile))
  val runs: List[Future[Result]] =
    (1 to settings.numberRuns).map(run => Future {
      val traceFile =
        if (run <= settings.numberTraces)
          Some(File(s"${settings.traceOutputPrefix}$run.txt"))
        else
          None
      runSimulation(settings, traceFile)
    }).toList
  Future.sequence(runs).onComplete {
    case Success(results) => System.exit(0)
    case Failure(error) => error.printStackTrace
  }
}