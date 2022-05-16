package viz

import spacenorm.{Configuration, State}
import spacenorm.Decode.{decodeConfiguration, decodeState}
import viz.Main.{newRunLoaded, nextStepLoaded}

object LogDecode:
  val configConsumer = LogConsumer(4, { lines =>
    decodeConfiguration(lines.mkString("\n")).foreach(newRunLoaded)
  })
  
  def stepConsumer(config: Configuration) = LogConsumer(1, { lines =>
    println("decode state")
    val x = decodeState(lines.mkString("\n"), config).foreach(nextStepLoaded)
    println(x.isDefined)
    x
  })

  def openNewRun(reader: LogReader): Unit =
    reader.openAndRead(configConsumer)

  def loadNextState(reader: LogReader, config: Configuration): Unit =
    reader.readMore(stepConsumer(config))

  def restartRun(reader: LogReader): Unit =
    reader.readFromStart(configConsumer)