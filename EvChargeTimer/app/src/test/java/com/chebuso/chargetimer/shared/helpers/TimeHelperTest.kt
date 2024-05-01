package com.chebuso.chargetimer.shared.helpers

import com.chebuso.chargetimer.shared.Time
import com.chebuso.chargetimer.shared.helpers.TimeHelper.formatAsMediumTime
import com.chebuso.chargetimer.shared.helpers.TimeHelper.formatAsShortDateTime
import com.chebuso.chargetimer.shared.helpers.TimeHelper.formatAsShortTime
import com.chebuso.chargetimer.shared.helpers.TimeHelper.toTime
import org.junit.Assert
import org.junit.Test
import java.util.Date

class TimeHelperTest {
    @Test
    fun toTime_should_return_time_for_more_than_week() {
        val actual = toTime(1342816363)
        Assert.assertEquals(Time(15, 13, 0), actual)
    }

    @Test
    fun toTime_should_return_time_within_3_days() {
        val actual = toTime(282698181)
        Assert.assertEquals(Time(3, 6, 31), actual)
    }

    @Test
    fun toTime_should_return_time_within_4_hours() {
        val actual = toTime(15119999)
        Assert.assertEquals(Time(4, 11), actual)
    }

    @Test
    fun should_format_as_medium_time() {
        val time = Date(1000*60)

        val actual = time.formatAsMediumTime()

        Assert.assertEquals("1:01:00 AM", actual)
    }

    @Test
    fun should_format_as_short_time() {
        val time = Date(1000*60)

        val actual = time.formatAsShortTime()

        Assert.assertEquals("1:01 AM", actual)
    }

    @Test
    fun should_format_as_short_date_time() {
        val time = Date(1000*60)

        val actual = time.formatAsShortDateTime()

        Assert.assertEquals("1/1/70, 1:01 AM", actual)
    }
}