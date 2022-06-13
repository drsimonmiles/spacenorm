package sim

import spacenorm.Behaviour

class ResultSuite extends munit.FunSuite {
  val countsA             = List(10, 20)
  def resultAt(tick: Int) = TickResult(tick, countsA, 0.0, 0.0)
  val tickResultsA        = List(resultAt(2), resultAt(1), resultAt(0))
  val runResultsA         = Result(1, tickResultsA)
  val tickResult3         = resultAt(3)

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
    assertEquals(runResultsA.addTick(tickResult3).ticks.size, 4)
    assertEquals(runResultsA.addTick(tickResult3).getTick(3).map(_.tick), Some(3))
    assertEquals(runResultsA.addTick(tickResult3).lastTick, 3)
    assertEquals(runResultsA.addTick(tickResult3).getOrderedTicks.map(_.tick), List(0, 1, 2, 3))
  }
}