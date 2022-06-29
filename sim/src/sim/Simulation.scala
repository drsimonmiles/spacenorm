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
@main def runExperiment(arguments: String*) = {
  val settingsFile = arguments.head
  val subfolders   = if (arguments.size > 1) Some(arguments.tail.toList) else None
  val inputFile    = File(settingsFile)
  val outputFolder =
    if (inputFile.isDirectory) File(inputFile.getName + "-out")
    else File(inputFile.getPath.dropRight(5) + "-out")
  val start = System.currentTimeMillis
  val tracesFolder = File(s"traces-$start")
  
  runExperimentsForSettings(File(settingsFile), subfolders, outputFolder, tracesFolder)
  println(s"Total time: ${System.currentTimeMillis - start}ms")
  println(s"Output stats written to ${outputFolder.getPath}")
  println(s"Traces (if any) written to ${tracesFolder.getPath}")
}

/**
 * Runs simulation experiments for the settings in the given file or, if the file is a folder, all settings
 * files in the directory tree.
 */
def runExperimentsForSettings(settingsFile: File,
                              subfoldersAllowed: Option[List[String]],
                              outputFolder: File,
                              tracesFolder: File): Unit =
  if (settingsFile.isDirectory) {
    settingsFile.listFiles.foreach { subfile =>
      if (subfile.isDirectory && subfoldersAllowed.map(_.contains(subfile.getName)).getOrElse(true))
        runExperimentsForSettings(subfile, subfoldersAllowed, File(outputFolder, subfile.getName), File(tracesFolder, subfile.getName))
      else
        runExperimentsForSettings(subfile, subfoldersAllowed, outputFolder, tracesFolder)
    }
  } else
    loadSettings(settingsFile) match {
      case SingleSettings(settings) => 
        runSimulationSet(settings, None, settingsFile.getName.dropRight(5), outputFolder, tracesFolder)
      case VariedSettings(variedParameter, settingsList) =>
        settingsList.foreach(settings => runSimulationSet(settings, Some(variedParameter), settingsFile.getName.dropRight(5), outputFolder, tracesFolder))
  }

/** Runs a batch of simulations with the same settings. */
def runSimulationSet(settings: Settings, variedParameter: Option[SettingName], settingsFilename: String, outputFolder: File, tracesFolder: File): Unit = {
  val output     = File(outputFolder, s"${statsFilePrefix(settings)}.csv")
  val paramValue = variedParameter.map(setting => s"-$setting=${setting.extractAsString(settings)}").getOrElse("")
  var seed       = settings.randomSeed

  outputFolder.mkdirs
  (1 to settings.numberRuns).foreach { run => {
    val start = System.currentTimeMillis
    val traceFile =
      if (run <= settings.numberTraces)
        Some(File(tracesFolder, s"$settingsFilename$paramValue-$run.trace"))
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
  val trace = traceFile.map { file =>
    file.getParentFile.mkdirs
    PrintWriter(FileWriter(file))
  }

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