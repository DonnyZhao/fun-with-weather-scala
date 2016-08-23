package com.funweather

import org.scalatest.Suites

class MySuites extends Suites (
  new PositionTest,
  new MeasurementTest,
  new ScenarioTest,
  new LocalTimeTest,
  new SimulatorTest
)