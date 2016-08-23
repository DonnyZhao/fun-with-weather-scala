package com.funweather

import org.apache.spark.mllib.tree.model.RandomForestModel

/**
  * Created by lshang on 8/23/16.
  */
class SimulatorTest extends UnitTest("Simulator") {

  it should "build and return four Random Forest models, also generate a sample with a new time&location pair" in {
    val models = Simulator.buildModels()
    models.size shouldEqual 4
    models.values.foreach(model => model.isInstanceOf[RandomForestModel] shouldEqual true)

    val sample = Simulator.generateSample(LocalTime("2016-08-22T12:00:00"), Position(-33.86, 151.21, 39.00))
    sample.size shouldEqual 4

    sample("Condition").isInstanceOf[Double] shouldEqual true
    sample("Temperature").isInstanceOf[Double] shouldEqual true
    sample("Pressure").isInstanceOf[Double] shouldEqual true
    sample("Humidity").isInstanceOf[Double] shouldEqual true
  }

}
