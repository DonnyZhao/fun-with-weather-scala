package com.probability

import probability_monad.Distribution
import probability_monad.Distribution._

/**
  * Created by lshang on 9/5/16.
  *
  * Example to generate a Markov Chain for weather condition
  */
object MarkovChainExamples extends App {

  def transition(condition: Int): Distribution[Int] = condition match {
    case 0 => discrete(0 -> 1 / 3, 1 -> 1 / 3, 2 -> 1 / 3)
    case 1 => discrete(0 -> 0.10,  1 -> 0.80,  2 -> 0.10)
    case 2 => discrete(0 -> 0.99,  1 -> 0.01,  2 -> 0.00)
  }

  def chain(num: Int): Distribution[List[Int]] = {
    val first = 0 // can be random
    always(List(first)).markov(num - 1)(sequence => for {
      next <- transition(sequence.last)
    } yield sequence :+ next)
  }

  val weatherCondition = chain(30).sample(1).flatten
  println(weatherCondition)

}
