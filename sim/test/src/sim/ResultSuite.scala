package sim

import spacenorm.Behaviour
import sim.Prebuilt.*

class ResultSuite extends munit.FunSuite:
  def resultAt(tick: Int) = TickResult(tick, behaviourCountsA, 0.0, 0.0)
  val tickResultsA        = List(resultAt(2), resultAt(1), resultAt(0))
  val runResultsA         = Result(1, tickResultsA)

  test("Last tick") {
    assertEquals(runResultsA.lastTick, 2)
  }

  test("Tick range") {
    assertEquals(runResultsA.tickRange.toList, List(0, 1, 2))
  }

  test("Retrieve tick result") {
    assertEquals(runResultsA.getTick(1).map(_.tick), Some(1))
  }

  test("Get ordered tick results") {
    assertEquals(runResultsA.getOrderedTicks.map(_.tick), List(0, 1, 2))
  }

  test("Add tick results") {
    val tickResult3       = resultAt(3)
    val updatedRunResults = runResultsA.addTick(tickResult3)
    assertEquals(updatedRunResults.ticks.size, 4)
    assertEquals(updatedRunResults.getTick(3).map(_.tick), Some(3))
    assertEquals(updatedRunResults.lastTick, 3)
    assertEquals(updatedRunResults.getOrderedTicks.map(_.tick), List(0, 1, 2, 3))
  }

  test("Add tick by state") {
    val updatedRunResults = runResultsA.addTick(3, stateA)
    assertEquals(updatedRunResults.ticks.size, 4)
    assertEquals(updatedRunResults.getTick(3).map(_.tick), Some(3))
    assertEquals(updatedRunResults.lastTick, 3)
    assertEquals(updatedRunResults.getOrderedTicks.map(_.tick), List(0, 1, 2, 3))
    assertEquals(updatedRunResults.getTick(3).map(_.prevalences), Some(List(500, 500)))
    assertEquals(updatedRunResults.getTick(3).map(_.meanUtility), Some(0.0))
  }

  test("Empty run result") {
    val runResultsB = Result(2)
    assertEquals(runResultsB.run, 2)
    assertEquals(runResultsB.ticks.size, 0)
    assertEquals(runResultsB.lastTick, -1)
    assert(runResultsB.tickRange.isEmpty)
  }

  test("Single state run result") {
    val newRunResult = Result(3, stateA)
    assertEquals(newRunResult.run, 3)
    assertEquals(newRunResult.ticks.size, 1)
    assertEquals(newRunResult.lastTick, 0)
    assertEquals(newRunResult.tickRange, (0 to 0))
    assertEquals(newRunResult.getOrderedTicks.map(_.tick), List(0))
    assertEquals(newRunResult.getTick(0).map(_.prevalences), Some(List(500, 500)))
    assertEquals(newRunResult.getTick(0).map(_.meanUtility), Some(0.0))
  }