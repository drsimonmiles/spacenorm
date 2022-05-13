package viz

final case class LogConsumer(initialLines: Int, processLines: Array[String] => Int)

object LogConsumer:
  def oneShot(initialLines: Int)(processLines: Array[String] => Unit): LogConsumer =
    LogConsumer(initialLines, lines => {processLines(lines); 0})