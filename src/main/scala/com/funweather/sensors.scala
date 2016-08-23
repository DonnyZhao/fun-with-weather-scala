package com.funweather

/**
  * Created by lshang on 8/21/16.
  *
  * Contains the definitions for sensor measurements
  */
case class Temperature(value: Double = Double.NaN, unit: String = "celsius") {}

case class Pressure(value: Double = Double.NaN, unit: String = "hPa") {}

case class Humidity(value: Double = Double.NaN, unit: String = "percent") {}


