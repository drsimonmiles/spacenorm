package study

import org.jfree.chart.plot.Plot
import spacenorm.SettingName

object PlottableSetting:
  def allComparableGroups(results: List[ResultsFile]): Map[SettingName, List[List[ResultsFile]]] =
    SettingName.values.map(setting => (setting, comparableGroups(setting, results))).toMap

  def comparableGroups(setting: SettingName, results: List[ResultsFile]): List[List[ResultsFile]] =
    results.groupBy(result => setting.ignoredIn(result.settings)).values.filter(_.size > 1).toList
