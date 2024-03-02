package com.chebuso.chargetimer.helpers

import com.chebuso.chargetimer.Time
import com.chebuso.chargetimer.helpers.TimeHelper.toTime
import org.junit.Assert
import org.junit.Test

class TimeHelperTest {
    @Test
    fun converting_more_than_week() {
        val actual = toTime(1342816363)
        Assert.assertEquals(Time(15, 13, 0), actual)
    }

    @Test
    fun converting_within_3_days() {
        val actual = toTime(282698181)
        Assert.assertEquals(Time(3, 6, 31), actual)
    }

    @Test
    fun converting_within_4_hours() {
        val actual = toTime(15119999)
        Assert.assertEquals(Time(4, 11), actual)
    }
}