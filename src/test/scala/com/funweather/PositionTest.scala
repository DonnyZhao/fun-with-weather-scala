package com.funweather

/**
  * Created by lshang on 8/20/16.
  */
class PositionTest extends UnitTest("Position") {

  it should "allow construction if range is valid" in {
    Position(0.0, 0.0, 0.0)
  }

  it should "deny construction if range is invalid" in {
    an [IllegalArgumentException] should be thrownBy {
      Position(-100.0, 0.0, 0.0)
    }
  }

}
