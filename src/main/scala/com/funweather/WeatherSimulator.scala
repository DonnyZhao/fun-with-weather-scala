package com.funweather

import java.time.{LocalDateTime, Month}

import breeze.numerics.floor
import breeze.stats.distributions.{RandBasis, ThreadLocalRandomGenerator, Uniform}
import org.apache.commons.math3.random.MersenneTwister

/**
  * Created by lshang on 8/20/16.
  *
  * A place for scenario definition and fun
  *
  */
object WeatherSimulator extends App {

  val startDate = LocalDateTime.of(2016, Month.AUGUST, 22, 0, 0, 0)

  // Scenario
  println("Scenario - Sydney weather over time")
  val numOfDays = 28
  List.range(0, numOfDays).foreach(i => {
    val timeStamp = startDate.plusDays(i).toString
    println(Measurement(i, Position(-33.86, 151.21, 39.0), LocalTime(timeStamp)).emit())
  })

  println("\n")

  // Scenario
  println("Weather of some random locations on the surface of planet Earth over time")
  val numOfLocations = 150
  implicit val randBasis: RandBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(123)))
  val uni = Uniform(0, LocationPosition.map.size)
  val sequence = uni.sample(numOfLocations).map(u => floor(u)).map(n => n.toInt)
  val locations = sequence.map(i => LocationPosition.map.toIndexedSeq.apply(i))

  List.range(0, locations.size).foreach(i => {
    val timeStamp = startDate.plusDays(i).toString
    val position = locations.apply(i)._2
    println(Measurement(i, position, LocalTime(timeStamp)).emit())
  })

}
