package sim

import sim.Metrics.*
import sim.Prebuilt.*

class MetricsSuite extends munit.FunSuite:
  test("Count behaviours in state") {
    assertEquals(behaviourCounts(stateA), List(500, 500))
  }

  test("Emergence of norms") {
    assert(!converged(behaviourCountsA))
    assert( converged(behaviourCountsB))
  }

  test("Diversity of behaviour") {
    assertEquals(diversity(List( 50, 50, 50, 50)), 1.0)
    assertEquals(diversity(List(200,  0,  0,  0)), 0.0)
  }