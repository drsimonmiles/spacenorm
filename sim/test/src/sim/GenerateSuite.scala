package sim

import sim.Files.loadSettings
import sim.Generate.*
import java.io.File
import scala.util.Random

class GenerateSuite extends munit.FunSuite:
  val settings   = loadSettings(File("sim/test/src/sim/test-config.toml"))
  val random   = Random()

  test("Configuration created") {
    newRunConfiguration(settings.head, random)
    assert(true)
  }

  test("State created") {
    val config = newRunConfiguration(settings.head, random)
    newState(config, random)
    assert(true)
  }