package spacenorm

object Debug:
  def checkDefined[T](name: String, value: Option[T]): Option[T] = {
    println(s"$name: ${value.isDefined}")
    value
  }

  def checkDecoded[T](name: String, wrapped: String => Option[T]): String => Option[T] =
    string => checkDefined(name, wrapped(string))
