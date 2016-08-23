package com.funweather

/**
  * Created by lshang on 8/21/16.
  */
class LocalTimeTest extends UnitTest("LocalTime") {

  it should "allow construction if type is valid" in {
    LocalTime("2016-08-22T12:00:00")
  }

}
