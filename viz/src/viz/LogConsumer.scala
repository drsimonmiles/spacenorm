package viz

final case class LogConsumer(initialLines: Int, processLines: Array[String] => Unit)
