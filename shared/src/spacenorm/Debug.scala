package spacenorm

object Debug:
  def checkDefined[T](name: String, value: Option[T]): Option[T] = {
    println(s"$name: ${value.isDefined}")
    value
  }
