package study

import java.io.File
import spacenorm.SettingName
import study.SystemCategory.{determineCategory, ignoreSystemDistinguishers}

final case class ResultsFolder(folder: File, files: List[ResultsFile]):
  /** The groups of results giving a comparison across system types */
  lazy val systemComparisons: List[ResultsComparison] =
    files
      .groupBy(result => ignoreSystemDistinguishers(result.settings))
      .values
      .filter(_.map(result => determineCategory(result.settings)).toSet.size > 1)
      .map(ResultsComparison.apply)
      .toList

  /** The groups of results giving a comparison across values of a setting */
  lazy val settingComparisons: Map[SettingName, List[ResultsComparison]] = 
    SettingName.values.map{ setting => (setting, 
      files
        .groupBy(result => setting.ignoredIn(result.settings))
        .values
        .filter(_.size > 1)
        .map(ResultsComparison.apply)
        .filterNot(comparison => systemComparisons.exists(comparison.subsetOf))
        .toList
    )}.toMap

  /** The results not in any comparative group */
  lazy val singletons: List[ResultsFile] = {
    val names = (settingComparisons.values.flatten ++ systemComparisons).flatMap(_.names).toSet
    files.filterNot { result => names.contains(result.file.getName) }
  }

object ResultsFolder:
  def apply(statsFolder: File): ResultsFolder =
    ResultsFolder(statsFolder, statsFolder.listFiles.map(ResultsFile.apply).toList)