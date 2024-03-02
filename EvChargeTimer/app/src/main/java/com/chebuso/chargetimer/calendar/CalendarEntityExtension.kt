package com.chebuso.chargetimer.calendar

import android.util.Log

fun List<CalendarEntity>.calendarsToString(): String {
    Log.d("CalendarEntityExtensions", "calendarsToString")
    val sb = StringBuilder()
    if(isEmpty()){
        sb.append("No calendars found")
    } else {
        forEach{ calendarToString(it, sb) }
    }
    return sb.toString()
}

private fun calendarToString(calendar: CalendarEntity, sb: StringBuilder) {
    sb.append(String.format("%d:name=%s, prim=%s, acc='%s'",
        calendar.id,
        calendar.displayName,
        calendar.isPrimary,
        calendar.accountName))

    if (calendar.ownerAccount != null)
       sb.append(", owner='${calendar.ownerAccount}'")
    if (calendar.accountType != null)
        sb.append(", type='${calendar.accountType}'")
    sb.append("; ")
}
