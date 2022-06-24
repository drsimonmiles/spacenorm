package sim

import sim.Files.loadSettings
import sim.Generate.*
import java.io.File
import scala.util.Random
import spacenorm.SingleSettings

class GenerateSuite extends munit.FunSuite:
  val settings = loadSettings(File("sim/test/src/sim/test-config.toml")).asInstanceOf[SingleSettings]
  val random   = Random()

  test("Configuration created") {
    newRunConfiguration(settings.settings, random)
    assert(true)
  }

  test("State created") {
    val config = newRunConfiguration(settings.settings, random)
    newState(config, random)
    assert(true)
  }