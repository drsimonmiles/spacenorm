package sim

import java.io.{File, FileWriter, PrintWriter}
import scala.util.Random
import sim.Files.{loadSettings, saveStats}
import sim.Generate.{newState, newRunConfiguration}
import sim.Process.runTick
import spacenorm.Encode.{encodeConfiguration, encodeSchemaVersion, encodeState}
import spacenorm.Settings

/*
  The main method to run the simulation. The command line takes an argument: the settings file.
*/
@main def runExperiment(settingsFile: String) = {
  val settings = loadSettings(File(settingsFile))
  var seed     = settings.randomSeed
  val results = (1 to settings.numberRuns).map(run => {
    val start = System.currentTimeMillis
    print(s"Run $run: ")
    val traceFile =
      if (run <= settings.numberTraces)
        Some(File(s"${settings.traceOutputPrefix}$run.txt"))
      else
        None
    val random = if (seed >= 0) Random(seed) else Random()
    val result = runSimulation(run, settings, traceFile, random)
    if (seed >= 0) seed += 1
    print(s" ${System.currentTimeMillis - start}ms")
    println
    result
  }).toList

  saveStats(results, File(settings.statsOutput))
}

def runSimulation(run: Int, settings: Settings, traceFile: Option[File], random: Random): Result = {
  val initialState = newState(newRunConfiguration(settings, random), random)
  val trace = traceFile.map(file => PrintWriter(FileWriter(file)))

  trace.foreach(_.println(encodeSchemaVersion))
  trace.foreach(_.println(encodeConfiguration(initialState.config)))
  val (finalState, result) =
    (1 to settings.numberTicks).foldLeft((initialState, Result(run, initialState))) { 
      case ((state, result), tick) =>
        print(".")
        trace.foreach(_.println(encodeState(state)))
        val nextState = runTick(state, random)
        (nextState, result.addTick(tick, nextState))
      }
  trace.foreach(_.println(encodeState(finalState)))
  trace.foreach(_.close)

  result
}

def checkTrace: Unit = {
  val source = scala.io.Source.fromFile("experiment1.txt")
  val lines = source.getLines.take(6)
  val config = spacenorm.Decode.decodeConfiguration(lines.mkString("\n"))
  println(config.map(_.netConstruction.toString).getOrElse("none"))
  source.close
  System.exit(0)
}