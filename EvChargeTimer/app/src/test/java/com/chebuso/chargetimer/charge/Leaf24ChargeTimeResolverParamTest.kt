package com.chebuso.chargetimer.charge

import com.chebuso.chargetimer.shared.Time
import com.chebuso.chargetimer.shared.helpers.TimeHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class Leaf24ChargeTimeResolverParamTest(
    private val remaining: Int,
    private val hours: Int,
    private val minutes: Int
){

    @Test fun return_time_when_calculating_remaining_battery() {
        val sut = LiIonChargeTimeResolver(
            PowerLine(220, 32),
            Battery(22.0, 12.0, remaining.toByte())
        )

        val actual = sut.getTimeToCharge()

        val actualTime = TimeHelper.toTime(actual)
        Assert.assertEquals(Time(hours, minutes), actualTime)
    }

    companion object{
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf (0, 4, 11),
            arrayOf (80, 1, 23),
            arrayOf (90, 0, 41),
            arrayOf (95, 0, 20),
        )
    }
}