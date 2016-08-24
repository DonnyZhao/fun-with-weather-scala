package com.funweather

import breeze.stats.distributions.{RandBasis, ThreadLocalRandomGenerator}
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
  }

}
