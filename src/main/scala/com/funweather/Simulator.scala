package com.funweather

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD
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

trait TrainingData {
  val root = "src/main/resources/training/"
}

trait SparkBase {
  private val master = "local[*]"
  private val appName = this.getClass.getSimpleName

  val conf = new SparkConf().setMaster(master).setAppName(appName)
}

trait Model {
  def build(training: RDD[LabeledPoint]): RandomForestModel
}

object Model {

  private class Condition extends Model {

    /**
      *
      * Build predictive model (Classification)
      *
      * @param training Training data set
      * @return Predictive model for weather condition
      */
    override def build(training: RDD[LabeledPoint]): RandomForestModel = {

      val numClasses = Condition.maxId
      val categoricalFeaturesInfo = Map[Int, Int]()
      val numTrees = 10 // can be more
      val featureSubsetStrategy = "auto"
      val impurity = "gini"
      val maxDepth = 4
      val maxBins = 32
      val model = RandomForest.trainClassifier(training, numClasses, categoricalFeaturesInfo,
        numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

      model
    }
  }

  private class Sensor extends Model {

    /**
      *
      * Build predictive model (Regression) for sensor Temperature/Pressure/Humidity
      *
      * @param training Training data set
      * @return Prediction model for weather sensors
      */
    override def build(training: RDD[LabeledPoint]): RandomForestModel = {

      val categoricalFeaturesInfo = Map[Int, Int]()
      val numTrees = 5 // can be more
      val featureSubsetStrategy = "auto"
      val impurity = "variance"
      val maxDepth = 4
      val maxBins = 32
      val model = RandomForest.trainRegressor(training, categoricalFeaturesInfo,
        numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

      model
    }

  }

  def apply(modelName: String): Model = {
    if (modelName == "Condition") new Condition
    else new Sensor
  }

}

object Simulator extends SparkBase with TrainingData {
  val models = buildModels()

  def loadTrainingData(sc: SparkContext, fileName: String): RDD[LabeledPoint] = {
    val path = root + fileName + ".txt"
    val data = MLUtils.loadLibSVMFile(sc, path)
    val splits = data.randomSplit(Array(0.7, 0.3), seed = 123L)
    val (trainingData, _) = (splits(0), splits(1))
    trainingData
  }

  /**
    * Build models for weather (Condition, Temperature/Pressure/Humidity)
    *
    * @return Predictive models
    */
  def buildModels(): Map[String, RandomForestModel] = {
    val sc = new SparkContext(conf)
    val models = List(Temperature, Pressure, Humidity, Condition)
      .map(v => v.toString).map(m => m -> Model.apply(m).build(loadTrainingData(sc, m))).toMap
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
