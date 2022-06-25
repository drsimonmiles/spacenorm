package sim

import sim.Files.loadSettings
import sim.Generate.{newRunConfiguration, newState}
import java.io.File
import scala.util.Random
import spacenorm.Decode.*
import spacenorm.Encode.*
import spacenorm.{Position, SingleSettings}

class DecodeSuite extends munit.FunSuite:
  val random = Random()

  test("Decode test configuration") {
    val settings   = loadSettings(File("sim/test/src/sim/test-config.toml")).asInstanceOf[SingleSettings]
    val config     = newRunConfiguration(settings.settings, random)
    val configCode = encodeConfiguration(config)
    assert(decodeConfiguration(configCode).nonEmpty)
  }

  test("Decode test state") {
    val settings   = loadSettings(File("sim/test/src/sim/test-config.toml")).asInstanceOf[SingleSettings]
    val config     = newRunConfiguration(settings.settings, random)
    val state      = newState(config, random)
    val stateCode  = encodeState(state)
    assert(decodeState(stateCode, config).nonEmpty)
  }

  test("Decode empty position list") {
    val code = ""
    val list: Option[List[Position]] = decodeList(decodePosition)(code)
    assertEquals(list, Some(Nil))
  }

  test("Decode base system configuration") {
    val settings   = loadSettings(File("sim/test/src/sim/test-base.toml")).asInstanceOf[SingleSettings]
    val config     = newRunConfiguration(settings.settings, random)
    val configCode = encodeConfiguration(config)
    assert(decodeConfiguration(configCode).nonEmpty)
  }
