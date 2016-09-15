package com.funweather

/**
  * Created by lshang on 8/21/16.
  */
class ScenarioTest extends UnitTest("Scenario") {

  it should "emit measurements for a scenario or batch" in {
    val measurements = List(
      Measurement(1, Position(0.0, 0.0, 0.0), LocalTime("2016-08-20T00:00:00")),
      Measurement(2, Position(-40.0, 135, 80.0), LocalTime("2016-08-20T00:00:00"))
    )
    Scenario("Sydney", measurements).measurements.foreach(m => m.condition.get shouldEqual Condition.Unknown)
    Scenario("Sydney", measurements).emit.foreach(m => m.condition.isEmpty shouldEqual false)
  }

}
