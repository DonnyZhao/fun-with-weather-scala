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

trait Model {
  def build(sc: SparkContext): RandomForestModel
}

object Model {

  private class Condition(name: String) extends Model {
    val path = TrainingData.PATH + name + ".txt"

    /**
      * Build predictive model (Classification)
      *
      * @param sc               SparkContext
      * @return Predictive model for weather condition
      */
    override def build(sc: SparkContext): RandomForestModel = {
      // data
      val data = MLUtils.loadLibSVMFile(sc, path)
      val splits = data.randomSplit(Array(0.7, 0.3), seed = 123L)
      val (trainingData, _) = (splits(0), splits(1))

      // train a RandomForest model - Classification
      val numClasses = Condition.maxId
      val categoricalFeaturesInfo = Map[Int, Int]()
      val numTrees = 10 // can be more
      val featureSubsetStrategy = "auto"
      val impurity = "gini"
      val maxDepth = 4
      val maxBins = 32
      val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
        numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

      model
    }
  }

  private class Sensor(name: String) extends Model {
    val path = TrainingData.PATH + name + ".txt"

    /**
      * Build predictive model (Regression) for sensor Temperature/Pressure/Humidity
      *
      * @param sc               SparkContext
      * @return Predictive model for weather sensors
      */
    override def build(sc: SparkContext): RandomForestModel = {
      // data
      val data = MLUtils.loadLibSVMFile(sc, path)
      val splits = data.randomSplit(Array(0.7, 0.3), seed = 123L)
      val (trainingData, _) = (splits(0), splits(1))

      // train a RandomForest model - Regression
      val categoricalFeaturesInfo = Map[Int, Int]()
      val numTrees = 5 // can be more
      val featureSubsetStrategy = "auto"
      val impurity = "variance"
      val maxDepth = 4
      val maxBins = 32
      val model = RandomForest.trainRegressor(trainingData, categoricalFeaturesInfo,
        numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

      model
    }

  }

  def apply(modelName: String): Model = {
    if (modelName == "Condition") new Condition(modelName)
    else new Sensor(modelName)
  }

}

object Simulator extends SparkBase {
  val models = buildModels()

  /**
    * Build models for weather (Condition, Temperature/Pressure/Humidity)
    *
    * @return Predictive models
    */
  def buildModels(): Map[String, RandomForestModel] = {
    val sc = new SparkContext(conf)
    val models = List(Temperature, Pressure, Humidity, Condition).map(v => v.toString).map(m => m -> Model.apply(m).build(sc)).toMap
    sc.stop()
    models
  }

  /**
    * Generate a sample using established predictive models
    *
    * @param localTime ISO8601 date time
    * @param position  A triple containing latitude, longitude and elevation
    * @return A generated weather sample containing CONDITION, TEMPERATURE, PRESSURE and HUMIDITY
    */
  def generateSample(localTime: LocalTime, position: Position): Map[String, Double] = {
    val dayOfWeek = localTime.localDateTime.getDayOfWeek.getValue //Feature engineering
    val newPoint = Vectors.dense(position.latitude, position.longitude, position.elevation, dayOfWeek)

    val predictions = models.map { case (k, v) => (k, v.predict(newPoint)) }
    predictions
  }

}
