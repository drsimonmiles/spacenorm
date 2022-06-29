package study

import java.io.File
import spacenorm.SettingName
import study.SystemCategory.{determineCategory, ignoreSystemDistinguishers}

final case class ResultsFolder(folder: File):
  lazy val files = folder.listFiles.filterNot(_.isDirectory).map(ResultsFile.apply).toList

  /** The groups of results giving a comparison across system types or setting values */
  lazy val comparisons: List[ResultsComparison] = {
    val systemComparisons = files
      .groupBy(result => ignoreSystemDistinguishers(result.settings))
      .values
      .filter(_.map(result => determineCategory(result.settings)).toSet.size > 1)
      .map(ResultsComparison.apply(_, None))
      .toList
    val settingComparisons = 
      SettingName.values.flatMap{ setting =>
        files
          .groupBy(result => setting.ignoredIn(result.settings))
          .values
          .filter(_.size > 1)
          .map(ResultsComparison.apply(_, Some(setting)))
          .filterNot(comparison => systemComparisons.exists(comparison.subsetOf))
      }.toList
    systemComparisons ++ settingComparisons
  }

  /** The results not in any comparative group */
  lazy val singletons: List[ResultsFile] = {
    val names = comparisons.flatMap(_.names).toSet
    files.filterNot { result => names.contains(result.file.getName) }
  }
