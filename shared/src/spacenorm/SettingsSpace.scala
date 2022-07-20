package spacenorm

/** A parameter space where settings can be varied by a particular parameter */
sealed trait SettingsSpace:
  def settingsList: List[Settings]

case class SingleSettings(settings: Settings) extends SettingsSpace:
  val settingsList = List(settings)

case class VariedSettings(variedParameter: SettingName, settingsList: List[Settings]) extends SettingsSpace
