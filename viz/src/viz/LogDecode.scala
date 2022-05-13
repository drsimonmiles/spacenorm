package viz

import spacenorm.{Configuration, State}
import spacenorm.Decode.{decodeConfiguration, decodeState}
import viz.Main.{newRunLoaded, nextStepLoaded}

object LogDecode:
  val configConsumer = LogConsumer.oneShot(4) { lines =>
    decodeConfiguration(lines.mkString("\n")).foreach(newRunLoaded)
  }
  def stepConsumer(config: Configuration) = LogConsumer.oneShot(1) { lines =>
    decodeState(lines.mkString("\n"), config).foreach(nextStepLoaded)
  }

  def openNewRun(reader: LogReader): Unit =
    reader.openAndRead(configConsumer)

  def loadNextState(reader: LogReader, config: Configuration): Unit =
    reader.readMore(stepConsumer(config))

  def restartRun(reader: LogReader): Unit =
    reader.readFromStart(configConsumer)