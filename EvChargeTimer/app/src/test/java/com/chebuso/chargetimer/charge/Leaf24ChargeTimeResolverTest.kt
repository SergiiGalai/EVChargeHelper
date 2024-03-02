package com.chebuso.chargetimer.charge

import com.chebuso.chargetimer.Time
import com.chebuso.chargetimer.helpers.TimeHelper.toTime
import org.junit.Assert
import org.junit.Test
import org.junit.runners.Parameterized

class Leaf24ChargeTimeResolverTest {

    @Test fun return_time_when_calculating_for_0pct_battery_home_charger() {
        val sut = LiIonChargeTimeResolver(
            PowerLine(220, 16),
            Battery(22.0, 12.0, 0))

        val actual = sut.getTimeToCharge()

        val actualTime = toTime(actual)
        Assert.assertEquals(Time(8, 23), actualTime)
    }
}

