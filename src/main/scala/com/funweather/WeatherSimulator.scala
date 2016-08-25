package com.funweather

import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, DateTimeParseException}

import breeze.numerics.floor
import breeze.stats.distributions.{Poisson, Uniform}

/**
  * Created by lshang on 8/20/16.
  *
  * A place for scenario definition and fun
  *
  */
object WeatherSimulator {

  def runScenarios(): Unit = {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    val startDate = LocalDateTime.now()

    // Scenario
    println("Scenario - Sydney weather over the next few days...")
    val numOfDays = Poisson(5).draw() + 1
    List.range(0, numOfDays).foreach(i => {
      val timeStamp = startDate.plusDays(i)
      val formattedDateTime = timeStamp.format(formatter)
      println(Measurement(i, Position(-33.86, 151.21, 39.0), LocalTime(formattedDateTime)).emit())
    })

    println("\n")

    // Scenario
    println("Scenario - weather of some random locations on the surface of planet Earth over the next few days...")
    val poi = Poisson(5)
    val numOfLocations = poi.draw() + 1
    val uni = Uniform(0, LocationPosition.map.size)
    val sequence = uni.sample(numOfLocations).map(u => floor(u)).map(n => n.toInt)
    val locations = sequence.map(i => LocationPosition.map.toIndexedSeq.apply(i))

    List.range(0, locations.size).foreach(i => {
      val timeStamp = startDate.plusDays(i)
      val formattedDateTime = timeStamp.format(formatter)
      val position = locations.apply(i)._2
      println(Measurement(i, position, LocalTime(formattedDateTime)).emit())
    })

  }

  def main(args: Array[String]): Unit = {
    val usage =
      """
      Usage:
      sbt "run-main com.funweather.WeatherSimulator [latitude longitude elevation time]"
      Example:
      sbt "run-main com.funweather.WeatherSimulator -33.86 151.21 39 2014-08-22T00:00:00"
      """

    if (args.length == 0) {
      runScenarios()
    } else if (args.length == 4) {
      try {
        val latitude = args(0).toDouble
        val longitude = args(1).toDouble
        val elevation = args(2).toDouble
        val localTime = args(3)
        println("\n")
        println(Measurement(0, Position(latitude, longitude, elevation), LocalTime(localTime)).emit())
        println("\n")
      } catch {
        case ex: NumberFormatException => {
          println("Wrong number format for position")
          println(usage)
        }
        case ex: DateTimeParseException => {
          println("Wrong format for time stamp")
          println(usage)
        }
        case ex: IllegalArgumentException => {
          println(ex.getMessage)
          println(usage)
        }

      }
    } else {
      println("Wrong number of input arguments")
      println(usage)
    }
  }

}
