package com.chebuso.chargetimer.charge

import com.chebuso.chargetimer.Time
import com.chebuso.chargetimer.helpers.TimeHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class Volt2014ChargeTimeResolverParamTest(
    private val remaining: Int,
    private val hours: Int,
    private val minutes: Int
){
    @Test fun return_time_when_calculating_remaining_battery() {
        val sut = LiIonChargeTimeResolver(
            PowerLine(220, 16),
            Battery(10.5, 12.0, remaining.toByte())
        )

        val actual = sut.getTimeToCharge()

        val actualTime = TimeHelper.toTime(actual)
        Assert.assertEquals(Time(hours, minutes), actualTime)
    }

    companion object{
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf (0, 4, 0),
            arrayOf (10, 3, 40),
            arrayOf (25, 3, 10),
            arrayOf (50, 2, 20),
            arrayOf (80, 1, 20),
            arrayOf (90, 0, 40),
            arrayOf (95, 0, 20),
        )
    }

}