package com.funweather

import breeze.stats.distributions.{RandBasis, ThreadLocalRandomGenerator, Uniform}
import org.apache.commons.math3.random.MersenneTwister
import org.scalatest.{FunSpec, Matchers}

class WeatherSimulatorSpec extends FunSpec with Matchers {

  describe("Test Breeze") {
    describe("Draw Poisson") {
      it("should return a random integer") {
        import breeze.stats.distributions.Poisson
        implicit val randBasis: RandBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(123)))
        val poi = Poisson(3.0)
        val x = poi.draw
        x should equal(4)
      }
    }

    describe("Draw Uniform") {
      it("should return random doubles") {
        val lo = 0.0
        val hi = 1.0

        val uni = Uniform(lo, hi)
        val numOfSamples = 10
        val samples = uni.sample(numOfSamples)

        samples shouldBe a [IndexedSeq[Double]]
        samples should have size numOfSamples

        samples.foreach(sample => sample should be (lo +- hi))
      }
    }
  }

}
