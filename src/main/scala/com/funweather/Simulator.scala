package com.funweather

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lshang on 8/21/16.
  *
  * A weather simulator to generate weather conditions and sensor measurements (Temperature/Pressure/Humidity).
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

  /**
    * Build predictive model (Classification)
    * @return Predictive model for weather condition
    */
  def build(trainingFileName: String): RandomForestModel = {
    val sc = new SparkContext(conf)

    // data
    val path = TrainingData.PATH + trainingFileName + ".txt"
    val data = MLUtils.loadLibSVMFile(sc, path.toString)
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
    * Build predictive model (Regression) for sensor Temperature/Pressure/Humidity
    * @param trainingFileName file path for the training data
    * @return Predictive model for weather sensors
    */
  def build(trainingFileName: String): RandomForestModel = {

    val sc = new SparkContext(conf)

    // data
    val path = TrainingData.PATH + trainingFileName + ".txt"
    val data = MLUtils.loadLibSVMFile(sc, path.toString)
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

object Simulator {
  val models = buildModels()

  private def buildModel(modelName: String): RandomForestModel = modelName match {
    case "Condition" => ConditionModel.build(modelName)
    case _ => SensorModel.build(modelName)
  }

  /**
    * Build models for weather (Condition, Temperature/Pressure/Humidity)
    * @return Predictive models
    */
  def buildModels(): Map[String, RandomForestModel] = {
    List("Temperature", "Pressure", "Humidity", "Condition").map(m => m -> buildModel(m)).toMap
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
