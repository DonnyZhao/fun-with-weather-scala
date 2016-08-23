package com.funweather

import java.time.LocalDate.parse
import java.time.format.DateTimeFormatter

/**
  *
  * @param timeStamp Local string time in the format of ISO8601 date time
  */
case class LocalTime(timeStamp: String) {
    //TODO: assert time stamp format
    val localDateTime = parse(timeStamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
