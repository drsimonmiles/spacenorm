package sim

import sim.Files.*
import java.io.File

class FilesSuite extends munit.FunSuite:
  test("Settings file loaded") {
    val settings   = loadSettings(File("sim/test/src/sim/test-config.toml"))
    assert(true)
  }