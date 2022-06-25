package study

final case class ResultsComparison(files: List[ResultsFile]):
  lazy val names: Set[String] =
    files.map(_.file.getName).toSet

  lazy val lastTick: Int =
    files.flatMap(_.results).map(_.lastTick).max

  def subsetOf(other: ResultsComparison): Boolean =
    names.subsetOf(other.names)

object ResultsComparison:
  def apply(singleton: ResultsFile): ResultsComparison =
    ResultsComparison(List(singleton))