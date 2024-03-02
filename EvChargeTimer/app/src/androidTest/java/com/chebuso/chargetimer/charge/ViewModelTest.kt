package com.chebuso.chargetimer.charge

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test

class ViewModelTest {

    @Test
    fun text_should_be_expected(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val sut = ViewModel(appContext, line(), battery())

        Assert.assertEquals(13230000, sut.millisToCharge)
        Assert.assertEquals("Car should be charged in 3h 40m", sut.chargedInText)
        Assert.assertEquals("10% battery remaining (1.1 of 10.5kWh)", sut.remainingEnergyText)
    }

    companion object{
        private fun battery() =
            Battery(10.5, 12.0, 10)

        private fun line(voltage: Int = 220, amperage: Int = 16) = PowerLine(voltage, amperage)
    }
}