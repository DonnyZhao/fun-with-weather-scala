package com.probability

import org.scalatest.{FunSpec, Matchers}
import probability_monad.Distribution._

/**
  * Created by lshang on 9/12/16.
  */
class ProbabilityMonadSpec extends FunSpec with Matchers {

  describe("Test Probability Monad") {
    describe("A fair coin") {
      it("should return a random integer of either 0 or 1") {
        val coin = discreteUniform(0 to 1)
        coin.sample(1).length should equal(1)
        coin.sample(1).head.isInstanceOf[Int] shouldEqual true
        coin.sample(1).head should (equal(0) or equal(1))
      }
    }

    describe("A fair dice") {
      it("should return a random integer from 1 to 6") {
        val dice = discreteUniform(1 to 6)
        dice.sample(1).length should equal(1)
        dice.sample(1).head.isInstanceOf[Int] shouldEqual true
        dice.sample(1).head should (be >= 1 and be <= 6)
      }
    }
  }

}
