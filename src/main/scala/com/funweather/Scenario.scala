package com.funweather

/**
  * Created by lshang on 8/21/16.
  *
  * A container of a group of measurements
  */
case class Scenario(name: String, measurements: List[Measurement]) {
  require(!name.isEmpty, "Need scenario name for measurement grouping")
  require(measurements.nonEmpty, "No measurement defined")

  def emit(): List[Measurement] = {
    measurements.map(measurement => measurement.emit())
  }

}
