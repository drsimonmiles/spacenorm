package viz

final case class LogConsumer(lines: Int, processLines: Array[String] => Unit)
