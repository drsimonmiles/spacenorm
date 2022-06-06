package viz

import spacenorm.{Configuration, State}
import spacenorm.Decode.{decodeConfiguration, decodeSchemaVersion, decodeState}
import viz.Main.{newRunLoaded, nextStepLoaded}

object LogDecode:
  val configConsumer = LogConsumer(7, { lines =>
    decodeSchemaVersion(lines.head)
    decodeConfiguration(lines.tail.mkString("\n")).foreach(newRunLoaded)
  })
  
  def stepConsumer(config: Configuration) = LogConsumer(1, { lines =>
    decodeState(lines.mkString("\n"), config).foreach(nextStepLoaded)
  })

  def openNewRun(reader: LogReader): Unit =
    reader.openAndRead(configConsumer)

  def loadNextState(reader: LogReader, config: Configuration): Unit =
    reader.readMore(stepConsumer(config))

  def restartRun(reader: LogReader): Unit =
    reader.readFromStart(configConsumer)