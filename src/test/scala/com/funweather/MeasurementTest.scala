package com.funweather

/**
  * Created by lshang on 8/20/16.
  */
class MeasurementTest extends UnitTest("Measurement") {

  it should "allow construction if value is valid" in {
    Measurement(1, Position(0.0, 0.0, 0.0), LocalTime("2016-08-20T00:00:00"))
  }

  it should "have empty weather data with construction" in {
    Measurement(1, Position(0.0, 0.0, 0.0), LocalTime("2016-08-20T00:00:00")).condition.isEmpty shouldEqual true
  }

  it should "emit weather data with emit()" in {
    val measurement = Measurement(1, Position(0.0, 0.0, 0.0), LocalTime("2016-08-20T00:00:00")).emit
    measurement.condition.isEmpty shouldEqual false
  }

  it should "deny emission with weather data in place" in {
    an[IllegalArgumentException] should be thrownBy {
      Measurement(1, Position(0.0, 0.0, 0.0), LocalTime("2016-08-20T00:00:00")).emit.emit
    }
  }

  it should "toString before and after emit" in {
    Measurement(1, Position(0.0, 0.0, 0.0), LocalTime("2016-08-20T00:00:00")).toString should not be empty
    Measurement(1, Position(0.0, 0.0, 0.0), LocalTime("2016-08-20T00:00:00")).emit.toString should not be empty
  }

}
