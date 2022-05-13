package viz

import org.scalajs.dom
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.{Event, File, FileReader}

trait LogConsumer:
  def initialLines: Int
  def processLines(lines: Array[String]): Int

class LogReader:
  val readChunkSize = 256
  private var currentFile: Option[File] = None
  private var nextStart: Int = 0
  private var readUnprocessed: String = ""
  private var neededLines: Int = 0

  def openAndRead(consumer: LogConsumer): Unit = {
    val input = dom.document.getElementById("file-input").asInstanceOf[Input]
    input.onchange = { (event: Event) =>
      currentFile = Some(input.files(0))
      readFromStart(consumer)
    }
    input.click
  }

  def readFromStart(consumer: LogConsumer): Unit = {
    nextStart = 0
    readUnprocessed = ""
    readMore(consumer)
  }

  def readMore(consumer: LogConsumer): Unit =
    readNextSlice(createReader(consumer))    

  private def createReader(consumer: LogConsumer): FileReader = {
    neededLines = consumer.initialLines
    val reader = new FileReader()
    reader.onload = readerEvent => {
      readUnprocessed = readUnprocessed + reader.result.toString
      //println(s"${currentFile.map(_.size).getOrElse(0)} $nextStart ${readUnprocessed.length} ${readUnprocessed.count(_ == '\n')}")
      println(readUnprocessed.take(300))
      processAndReadToCompletion(reader, consumer)
    }
    reader
  }

  private def readNextSlice(reader: FileReader): Unit =
    currentFile.foreach { file =>
      if (nextStart < file.size) {
        val slice = file.slice(nextStart, (nextStart + readChunkSize).min(file.size.toInt))
        nextStart += readChunkSize
        reader.readAsText(slice, "UTF-8")
      } else
        reader.abort
    }

  private def processAndReadToCompletion(reader: FileReader, consumer: LogConsumer): Unit =
    if (readUnprocessed.count(_ == '\n') < neededLines)
      readNextSlice(reader)
    else {
      val lines = readUnprocessed.split("\n")
      readUnprocessed = lines.slice(neededLines, lines.length).mkString("\n")
      neededLines = consumer.processLines(lines.slice(0, neededLines))
      if (neededLines > 0)
        processAndReadToCompletion(reader, consumer)
      else
        reader.abort
    }