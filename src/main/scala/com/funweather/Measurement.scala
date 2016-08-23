package com.funweather

import com.funweather.Condition.Condition

/**
  * Created by lshang on 8/20/16.
  *
  * A container for measurement in the format of
  * Location Position Local Time Conditions Temperature Pressure Humidity
  * example:
  * Sydney|-33.86,151.21,39|2015-12-23T05:02:12Z|Rain|+12.5|1004.3|97
  *
  * @param number Measurement number or id
  * @param position Position of the measurement containing latitude, longitude and elevation
  * @param location Location of the measurement
  * @param condition Weather condition
  * @param temperature Weather sensor measurement
  * @param pressure Weather sensor measurement
  * @param humidity Weather sensor measurement
  */
case class Measurement(number: Int,
                       position: Position, localTime: LocalTime,
                       location: Option[String] = None,
                       condition: Option[Condition] = None,
                       temperature: Option[Temperature] = Some(Temperature()),
                       pressure: Option[Pressure] = Some(Pressure()),
                       humidity: Option[Humidity] = Some(Humidity())
                      ) {
  measurement =>

  def emit(): Measurement = {
    require(measurement.condition.isEmpty, "Measurement has weather data already")
    require(measurement.temperature.get.value.isNaN, "Measurement has weather data already")
    require(measurement.pressure.get.value.isNaN, "Measurement has weather data already")
    require(measurement.humidity.get.value.isNaN, "Measurement has weather data already")

    val sample = Simulator.generateSample(localTime, position)

    val cond = Option(Condition.apply(sample("Condition").toInt))
    val temperature = Option(Temperature(sample("Temperature")))
    val pressure = Option(Pressure(sample("Pressure")))
    val humidity = Option(Humidity(sample("Humidity")))

    val mappedLocation = LocationPosition.map.map(_.swap).get(position).orElse(location)

    Measurement(number, position, localTime, mappedLocation, cond, temperature, pressure, humidity)
  }

  override def toString: String = {
    "%d|%s|%s|%s|%s|%.2f|%.2f|%.2f".format(number, location.getOrElse("None"), position.toString, localTime.timeStamp,
      condition.getOrElse("None"), temperature.get.value, pressure.get.value, humidity.get.value)
  }

}
