package sim

import sim.Files.loadSettings
import sim.Generate.{newRunConfiguration, newState}
import java.io.File
import scala.util.Random
import spacenorm.Decode.*
import spacenorm.Encode.*
import spacenorm.SingleSettings

class DecodeSuite extends munit.FunSuite:
  val settings   = loadSettings(File("sim/test/src/sim/test-config.toml")).asInstanceOf[SingleSettings]
  val random     = Random()
  val config     = newRunConfiguration(settings.settings, random)
  val state      = newState(config, random)
  val configCode = encodeConfiguration(config)
  val stateCode  = encodeState(state)

  test("Decode configuration") {
    assert(decodeConfiguration(configCode).nonEmpty)
  }

  test("Decode state") {
    assert(decodeState(stateCode, config).nonEmpty)
  }
