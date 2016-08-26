package com.funweather

import org.apache.spark.mllib.tree.configuration.Algo
import org.apache.spark.mllib.tree.model.RandomForestModel


/**
  * Created by lshang on 8/23/16.
  */
class SimulatorTest extends UnitTest("Simulator") {

  it should "generate a sample with a new time&location pair" in {
    val sample = Simulator.generateSample(LocalTime("2016-08-22T12:00:00"), Position(-33.86, 151.21, 39.00))
    sample.size shouldEqual 4

    sample("Condition").isInstanceOf[Double] shouldEqual true
    sample("Temperature").isInstanceOf[Double] shouldEqual true
    sample("Pressure").isInstanceOf[Double] shouldEqual true
    sample("Humidity").isInstanceOf[Double] shouldEqual true
  }

  it should "produce the same result with a repeating time&location pair" in {
    val localTime = LocalTime("2016-08-22T12:00:00")
    val position = Position(-33.86, 151.21, 39.00)
    val sample = Simulator.generateSample(localTime, position)
    val sampleAgain = Simulator.generateSample(localTime, position)
    sample.toSet.diff(sampleAgain.toSet).size shouldEqual 0
  }

  it should "build and return one classification model, three regression models" in {
    val models = Simulator.buildModels()
    models.size shouldEqual 4

    models.values.foreach(model => model.isInstanceOf[RandomForestModel] shouldEqual true)

    models("Condition").algo.compare(Algo.Classification) shouldEqual 0
    models("Temperature").algo.compare(Algo.Regression) shouldEqual 0
    models("Pressure").algo.compare(Algo.Regression) shouldEqual 0
    models("Humidity").algo.compare(Algo.Regression) shouldEqual 0
  }

}
