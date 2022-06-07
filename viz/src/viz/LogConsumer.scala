package viz

/** Represents the requirement to read a given number of lines from a visualisation trace and the function to 
 * process those lines.
 */
final case class LogConsumer(lines: Int, processLines: Array[String] => Unit)
