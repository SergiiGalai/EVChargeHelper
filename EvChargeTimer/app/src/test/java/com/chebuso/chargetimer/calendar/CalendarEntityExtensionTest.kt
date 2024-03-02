package com.chebuso.chargetimer.calendar

import org.junit.Assert
import org.junit.Test

class CalendarEntityExtensionTest {

    @Test fun should_return_not_found_when_calendar_list_is_empty(){
        val sut = ArrayList<CalendarEntity>()

        val actual = sut.calendarsToString()

        Assert.assertEquals("No calendars found", actual)
    }

    @Test fun should_return_string_representation_when_calendar_list_contains_items(){
        val sut = ArrayList<CalendarEntity>()
        sut.add(CalendarEntity().apply {
            id = 1
            isPrimary = true
            accountName = "account1"
            accountType = "type1"
            displayName = "display1"
        })
        sut.add(CalendarEntity().apply {
            id = 2
            isPrimary = false
            accountName = "account2"
        })
        val expected = "1:name=display1, prim=true, acc='account1', type='type1'; 2:name=null, prim=false, acc='account2'; "

        val actual = sut.calendarsToString()

        Assert.assertEquals(expected, actual)
    }
}