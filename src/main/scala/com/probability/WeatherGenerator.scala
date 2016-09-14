package com.probability

import java.io._
import java.time.LocalDateTime

import com.funweather.Condition.Condition
import com.funweather.Sensor.Sensor
import com.funweather._
import probability_monad.Distribution
import probability_monad.Distribution._

/**
  * Created by lshang on 9/5/16.
  *
  * Generate synthetic historical data for weather condition and sensor measurements (Temperature/Humidity/Pressure)
  *
  * Simulation methodology:
  * Two many interacting variables, we built probabilistic models af aggregated behaviours.
  * The weather model implemented here is driven by a Markov Chain.
  * Steps:
  *     1. Generate a Markov Chain for weather conditions
  *     2. Label the states or events in the Markov Chain with time stamps of certain resolution
  *     3. Generate the sensor measurements conditioned on the weather condition
  *     4. Output the generated data to LIBSVM format files
  *
  */
object WeatherGenerator extends App {

  /**
    * Transition matrix for Markov Chain
    *
    * @param condition Current state for the weather condition
    * @return The next state for the weather condition
    */
  def transition(condition: Int): Distribution[Int] = condition match {
    case 0 => discrete(0 -> 1 / 3, 1 -> 1 / 3, 2 -> 1 / 3)
    case 1 => discrete(0 -> 0.10, 1 -> 0.80, 2 -> 0.10)
    case 2 => discrete(0 -> 0.99, 1 -> 0.01, 2 -> 0.00)
  }

  /**
    * Weather model for CONDITION
    *
    * @param num Number of events in the Markov Chain
    * @return A sequence of events simulating the weather condition
    */
  def chain(num: Int): Distribution[List[Int]] = {
    val first = 0 // can be random
    always(List(first)).markov(num - 1)(sequence => for {
      next <- transition(sequence.last)
    } yield sequence :+ next)
  }

  /**
    * Weather model for TEMPERATURE - Gaussian distributed
    *
    * @param condition The weather condition that the temperature is conditioned on
    * @return A temperature value
    */
  def generateTemperature(condition: Condition) = condition match {
    case Condition.Snow => 23.5 + normal.sample(1).head
    case Condition.Rain => 30.5 + normal.sample(1).head
    case Condition.Sunny => 5.0 + normal.sample(1).head
  }

  /**
    * Weather model for HUMIDITY - Uniform distributed
    *
    * @param condition The weather condition that the humidity is conditioned on
    * @return A humidity value
    */
  def generateHumidity(condition: Condition) = condition match {
    case Condition.Snow => (70.0 - 40.0) * uniform.sample(1).head + 40.0
    case Condition.Rain => (95.0 - 70.0) * uniform.sample(1).head + 70.0
    case Condition.Sunny => (70.0 - 30.0) * uniform.sample(1).head + 30.0
  }

  /**
    * Weather model for PRESSURE - Gaussian distributed
    *
    * @param condition The weather condition that the pressure is conditioned on
    * @return A pressure value
    */
  def generatePressure(condition: Condition) = condition match {
    case Condition.Snow => 800.0 + 2.0 * normal.sample(1).head
    case Condition.Rain => 900.0 + 1.5 * normal.sample(1).head
    case Condition.Sunny => 700.0 + 0.2 * normal.sample(1).head
  }

  /**
    * Generate a sensor sample based on the sensor type
    *
    * @param sensor    Sensor type
    * @param condition Weather condition
    * @return A sensor measurement
    */
  def generateSensorSample(sensor: Sensor, condition: Condition) = sensor match {
    case Sensor.Temperature => generateTemperature(condition)
    case Sensor.Humidity => generateHumidity(condition)
    case Sensor.Pressure => generatePressure(condition)
  }

  /**
    * Write training data to a file
    *
    * @param fileName The file name for the sample to write to
    * @param sample   A sample
    */
  def writeTrainingData(fileName: String, sample: String): Unit = {
    val ROOT = "src/main/resources/"
    val writer = new BufferedWriter(new FileWriter(ROOT + fileName + ".txt", true))
    writer.write(sample)
    writer.close()
  }

  /**
    * Format the generated data sample into LIBSVM format
    *
    * @param weather  A weather variable, any of CONDITION, TEMPERATURE/HUMIDITY/PRESSURE
    * @param position A triplet of latitude, longitude and elevation
    * @return A sample in LIBSVM format
    */
  def formatLIBSVM(weather: Double, position: Position, time: Int): String = {
    "%.2f".format(weather) + " " +
      "1:" + "%.2f".format(position.latitude) + " " +
      "2:" + "%.2f".format(position.longitude) + " " +
      "3:" + "%.2f".format(position.elevation) + " " +
      "4:" + "%d".format(time) +
      "\n"
  }

  /**
    * Generate a training sample and write to files
    *
    * Effectively, we establish a statistic model for each of the weather variables (Condition,
    * Temperature/Humidity/Pressure) which is dependent of several independent variables.
    *
    * Independent variable ~ Dependent variables
    * ------------------------------------------------------------
    * Weather Condition ~  Latitude + Longitude + Elevation + Time
    * Temperature ~  Latitude + Longitude + Elevation + Time
    * Humidity ~  Latitude + Longitude + Elevation + Time
    * Pressure ~  Latitude + Longitude + Elevation + Time
    *
    * @param weatherCondition Weather condition as the main driver of the weather model
    * @param position         a triplet of latitude, longitude and elevation
    */
  def generateSample(weatherCondition: (Condition, LocalDateTime), position: Position): Unit = {
    Sensor.values
      .map(s => s -> generateSensorSample(s, weatherCondition._1))
      .foreach { case (k, v) => writeTrainingData(k.toString, formatLIBSVM(v, position, weatherCondition._2.getDayOfWeek.getValue)) }

    val fileName = "Condition"
    writeTrainingData(fileName, formatLIBSVM(weatherCondition._1.id, position, weatherCondition._2.getDayOfWeek.getValue))
  }

  // --------------------------------------------------------
  // Generate Training Sets
  // --------------------------------------------------------
  // Markov Chain for weather conditions
  val numOfSamples = 30
  val states = chain(numOfSamples).sample(1).flatten.map(s => Condition(s))

  // Time stamps for the Markov Chain
  val startDate = LocalDateTime.of(2015, 1, 1, 0, 0, 0) // Historical data starting from some time last year
  val timeStamps = states.indices.map(i => startDate.plusDays(i))
  val timeStampedWeatherConditions = states.zip(timeStamps)

  // Generate training sets
  // Assume all locations share the same Markov Chain
  LocationPosition.map.values.foreach { v =>
    timeStampedWeatherConditions.foreach(c => generateSample(c, v))
  }

}
