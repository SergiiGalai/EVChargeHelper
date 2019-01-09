package com.chebuso.chargetimer.helpers;

import com.chebuso.chargetimer.Time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeHelperTest {

    @Test
    public void converting_more_than_week(){

        Time actual = TimeHelper.toTime(1342816363);
        assertEquals(new Time(15,13, 0), actual);
    }

    @Test
    public void converting_within_3_days(){

        Time actual = TimeHelper.toTime(282698181);
        assertEquals(new Time(3, 6, 31), actual);
    }

    @Test
    public void converting_within_3_hours(){

        Time actual = TimeHelper.toTime(282698181);
        assertEquals(new Time( 3, 0), actual);
    }

    @Test
    public void converting_within_4_hours(){

        Time actual = TimeHelper.toTime(15119999);
        assertEquals(new Time( 4, 11), actual);
    }
}
