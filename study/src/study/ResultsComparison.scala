package study

import spacenorm.SettingName
import study.SystemCategory.determineCategory

final case class ResultsComparison(files: List[ResultsFile], comparator: Option[SettingName]):
  lazy val names: Set[String] =
    files.map(_.file.getName).toSet

  lazy val orderedFiles: List[ResultsFile] =
    files.sortBy { result =>
      comparator match {
        case Some(variable) => variable.extractAsDouble(result.settings)
        case None => determineCategory(result.settings).ordinal.toDouble
      }
    }

  lazy val lastTick: Int =
    files.flatMap(_.results).map(_.lastTick).max

  lazy val prefix = comparator.map(_.wildcardedPrefix(files.head.prefix, "varied")).getOrElse("system")

  def subsetOf(other: ResultsComparison): Boolean =
    names.subsetOf(other.names)

object ResultsComparison:
  def apply(singleton: ResultsFile): ResultsComparison =
    ResultsComparison(List(singleton), None)