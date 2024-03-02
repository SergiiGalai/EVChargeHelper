package com.chebuso.chargetimer.charge

import org.junit.Assert
import org.junit.Test

class Volt2014ChargeTimeResolverTest {

    @Test fun return_zero_time_when_amperage_zero() {
        val sut = LiIonChargeTimeResolver(line(amperage = 0), battery())

        val actual = sut.getTimeToCharge()

        Assert.assertEquals(0, actual)
    }

    @Test fun return_zero_time_when_voltage_zero() {
        val sut = LiIonChargeTimeResolver(line(0), battery())

        val actual = sut.getTimeToCharge()

        Assert.assertEquals(0, actual)
    }

    companion object{
        private fun battery() =
            Battery(10.5, 12.0, 10)

        private fun line(voltage: Int = 220, amperage: Int = 16) = PowerLine(voltage, amperage)
    }
}

