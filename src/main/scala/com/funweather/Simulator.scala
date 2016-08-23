package com.funweather

import java.nio.file.Paths

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lshang on 8/21/16.
  *
  * A weather simulator to generate weather conditions and sensor measurements (Temperature/Pressure/Humidity)
  * Predictive models are established based on training data sets. Simulated weather data can then be produced
  * with a new time&position pair.
  *
  * The pick of the Machine Learning algorithm is Random Forests
  * The training data sets are in LIBSVM format
  */

object TrainingData {
  val PATH = "src/main/resources/training/"
}

trait SparkBase {
  private val master = "local[*]"
  private val appName = this.getClass.getSimpleName

  val conf = new SparkConf().setMaster(master).setAppName(appName)
}

object ConditionModel extends SparkBase {
  val model = build()

  /**
    * Build predictive model (Classification)
    * @return
    */
  def build(): RandomForestModel = {
    val sc = new SparkContext(conf)

    // data
    val fileName = "condition.txt"
    val trainingFilePath = Paths.get(TrainingData.PATH, fileName).toString
    val data = MLUtils.loadLibSVMFile(sc, trainingFilePath)
    val splits = data.randomSplit(Array(0.7, 0.3), seed = 123L)
    val (trainingData, _) = (splits(0), splits(1))

    // train a RandomForest model
    val numClasses = Condition.maxId
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 10 // can be more
    val featureSubsetStrategy = "auto"
    val impurity = "gini"
    val maxDepth = 4
    val maxBins = 32
    val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

    sc.stop()
    model
  }
}

object SensorModel extends SparkBase {

  /**
    * Build predictive models for sensors (Temperature/Pressure/Humidity)
    * @param trainingFilePath file path for the training data
    * @return
    */
  def build(trainingFilePath: String): RandomForestModel = {
    val sc = new SparkContext(conf)

    // data
    val data = MLUtils.loadLibSVMFile(sc, trainingFilePath)
    val splits = data.randomSplit(Array(0.7, 0.3), seed = 123L)
    val (trainingData, _) = (splits(0), splits(1))

    // train a RandomForest model
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 5 // can be more
    val featureSubsetStrategy = "auto"
    val impurity = "variance"
    val maxDepth = 4
    val maxBins = 32
    val model = RandomForest.trainRegressor(trainingData, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

    sc.stop()
    model
  }
}

object TemperatureModel {
  val model = build()

  /**
    * Build predictive model (Regression)
    * @return
    */
  def build(): RandomForestModel = {
    val fileName = "temperature.txt"
    val path = Paths.get(TrainingData.PATH, fileName)
    SensorModel.build(path.toString)
  }
}

object PressureModel {
  val model = build()

  /**
    * Build predictive model (Regression)
    * @return
    */
  def build(): RandomForestModel = {
    val fileName = "pressure.txt"
    val path = Paths.get(TrainingData.PATH, fileName)
    SensorModel.build(path.toString)
  }
}

object HumidityModel {
  val model = build()

  /**
    * Build predictive model (Regression)
    * @return
    */
  def build(): RandomForestModel = {
    val fileName = "humidity.txt"
    val path = Paths.get(TrainingData.PATH, fileName)
    SensorModel.build(path.toString)
  }
}

object Simulator {
  val models = buildModels()

  /**
    * Build models for weather (Condition, Temperature/Pressure/Humidity)
    * @return
    */
  def buildModels(): Map[String, RandomForestModel] = {
    Map(
      "Condition" -> ConditionModel.model,
      "Temperature" -> TemperatureModel.model,
      "Pressure" -> PressureModel.model,
      "Humidity" -> HumidityModel.model
    )
  }

  /**
    * Generate a sample using established predictive models
    * @param localTime ISO8601 date time
    * @param position A triple containing latitude, longitude and elevation
    * @return A generated weather sample containing CONDITION, TEMPERATURE, PRESSURE and HUMIDITY
    */
  def generateSample(localTime: LocalTime, position: Position): Map[String, Double] = {
    val dayOfWeek = localTime.localDateTime.getDayOfWeek.getValue //Feature engineering
    val newPoint = Vectors.dense(position.latitude, position.longitude, position.elevation, dayOfWeek)

    val predictions = models.map { case (k, v) => (k, v.predict(newPoint)) }
    predictions
  }

}
