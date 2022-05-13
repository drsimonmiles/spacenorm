package viz

import spacenorm.{Configuration, State}
import spacenorm.Decode.{decodeConfiguration, decodeState}
import viz.Main.{newRunLoaded, nextStepLoaded}

object LogDecode:
  def openNewRun(reader: LogReader): Unit =
    reader.openAndRead {
      new LogConsumer {
        val initialLines = 4
        def processLines(lines: Array[String]): Int = {
          println("Processing new run lines")
          println(decodeConfiguration(lines.mkString("\n")).isDefined)
          decodeConfiguration(lines.mkString("\n")).foreach(newRunLoaded)
          0
        }
      }
    }

  def loadNextState(reader: LogReader, config: Configuration): Unit =
    reader.openAndRead {
      new LogConsumer {
        val initialLines = 1
        def processLines(lines: Array[String]): Int = {
          decodeState(lines.mkString("\n"), config).foreach(nextStepLoaded)
          0
        }
      }
    }

  def restartRun(reader: LogReader): Unit =
    reader.readFromStart {
      new LogConsumer {
        val initialLines = 4
        def processLines(lines: Array[String]): Int = {
          decodeConfiguration(lines.mkString("\n")).foreach(newRunLoaded)
          0
        }
      }
    }
