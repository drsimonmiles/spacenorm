package sim

import java.io.{File, FileWriter, PrintWriter}
import scala.util.Random
import sim.Files.{loadSettings, saveStats, statsFilePrefix}
import sim.Generate.{newState, newRunConfiguration}
import sim.Process.runTick
import spacenorm.Encode.{encodeConfiguration, encodeSchemaVersion, encodeState}
import spacenorm.{Settings, SettingName, SingleSettings, State, VariedSettings}

// This file contains the model-independent logic that initiates the simulation.

/**
  The main method to run the simulation.
  The command line takes an argument: a settings file or a folder containing settings files (possibly in subfolders).
  The settings file defines a space of settings, for each which a batch of simulations are run.
*/
@main def runExperiment(settingsFile: String) =
  runExperimentsForSettings(File(settingsFile))

/**
 * Runs simulation experiments for the settings in the given file or, if the file is a folder, all settings
 * files in the directory tree.
 */
def runExperimentsForSettings(settingsFile: File): Unit =
  if (settingsFile.isDirectory) {
    settingsFile.listFiles.foreach(runExperimentsForSettings)
  } else
    loadSettings(settingsFile) match {
      case SingleSettings(settings) => 
        runSimulationSet(settings, None)
      case VariedSettings(variedParameter, settingsList) =>
        settingsList.foreach(settings => runSimulationSet(settings, Some(variedParameter)))
  }

/** Runs a batch of simulations with the same settings. */
def runSimulationSet(settings: Settings, variedParameter: Option[SettingName]): Unit = {
  val outputDir  = File(settings.statsOutput)
  val output     = File(outputDir, s"${statsFilePrefix(settings)}.csv")
  val paramValue = variedParameter.map(_.extractAsString(settings)).map(s => s"-$s").getOrElse("")
  var seed       = settings.randomSeed

  outputDir.mkdirs
  (1 to settings.numberRuns).foreach { run => {
    val start = System.currentTimeMillis
    val traceFile =
      if (run <= settings.numberTraces)
        Some(File(s"${settings.traceOutputPrefix}$paramValue-$run.trace"))
      else
        None
    val random = if (seed >= 0) Random(seed) else Random()
    val result = runSimulation(run, settings, traceFile, random)
    if (seed >= 0) seed += 1
    print(s" ${System.currentTimeMillis - start}ms")
    println
    saveStats(result, output)
  }}
}

/**
 * Performs one run of the simulation iterating, updating the state each tick.
 */
def runSimulation(run: Int, settings: Settings, traceFile: Option[File], random: Random): Result = {
  val initialState = newState(newRunConfiguration(settings, random), random)
  val trace = traceFile.map(file => PrintWriter(FileWriter(file)))

  trace.foreach(_.println(encodeSchemaVersion))
  trace.foreach(_.println(encodeConfiguration(initialState.config)))
  val (finalState, _, finalResult) =
    (1 to settings.numberTicks).foldLeft[(State, Option[State], Result)]
      ((initialState, None, Result(run, initialState))) { 
        case ((state, previousState, result), tick) =>
          print(".")
          trace.foreach(_.println(encodeState(state)))
          val nextState = runTick(state, previousState, random)
          (nextState, Some(state), result.addTick(tick, nextState))
        }
  trace.foreach(_.println(encodeState(finalState)))
  trace.foreach(_.close)

  finalResult
}