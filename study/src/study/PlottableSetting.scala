package study

import spacenorm.SettingName

object PlottableSetting:
  def allComparableGroups(results: List[ResultsFile]): Map[SettingName, List[List[ResultsFile]]] =
    SettingName.values.map(setting => (setting, comparableGroups(setting, results))).toMap

  def comparableGroups(setting: SettingName, results: List[ResultsFile]): List[List[ResultsFile]] =
    results.groupBy(result => setting.ignoredIn(result.settings)).values.filter(_.size > 1).toList

  def notInGroups(results: List[ResultsFile], groups: Map[SettingName, List[List[ResultsFile]]]): List[ResultsFile] = {
    val names = groups.values.flatten.flatten.map(_.file.getName).toSet
    results.filterNot { result => names.contains(result.file.getName) }
  }