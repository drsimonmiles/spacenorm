package viz

import spacenorm.{Configuration, State}
import spacenorm.Decode.{decodeConfiguration, decodeState}

object LogDecode:
  def openNewRun(reader: LogReader): Option[Configuration] = {
    var config: Option[Configuration] = None
    reader.openAndRead {
      new LogConsumer {
        val initialLines = 4
        def processLines(lines: Array[String]): Int = {
          config = decodeConfiguration(lines.mkString("\n"))
          0
        }
      }
    }
    config
  }

  def loadNextState(reader: LogReader, config: Configuration): Option[State] = {
    var state: Option[State] = None
    reader.openAndRead {
      new LogConsumer {
        val initialLines = 1
        def processLines(lines: Array[String]): Int = {
          state = decodeState(lines.mkString("\n"), config)
          0
        }
      }
    }
    state
  }

  def restartRun(reader: LogReader): Option[Configuration] = {
    var config: Option[Configuration] = None
    reader.readFromStart {
      new LogConsumer {
        val initialLines = 4
        def processLines(lines: Array[String]): Int = {
          config = decodeConfiguration(lines.mkString("\n"))
          0
        }
      }
    }
    config
  }
