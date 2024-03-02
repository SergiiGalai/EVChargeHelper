package com.chebuso.chargetimer.settings

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test

class SharedPreferenceSettingsReaderTest {
    @Test
    fun integer_type_setting_should_be_read_from_preferences(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val sut = SharedPreferenceSettingsReader(appContext)

        val actual = sut.getDefaultVoltage()

        Assert.assertEquals(220, actual)
    }

    @Test
    fun double_type_setting_should_be_read_from_preferences(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val sut = SharedPreferenceSettingsReader(appContext)

        val actual = sut.getBatteryCapacity()

        Assert.assertEquals(10.5, actual, 0.001)
    }
}