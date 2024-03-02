package com.chebuso.chargetimer.calendar.dal

import android.database.Cursor
import android.provider.CalendarContract
import com.chebuso.chargetimer.calendar.CalendarEntity

internal class CalendarEntityReader {
    fun fromCursorPosition(cur: Cursor): CalendarEntity {
        val idIndex = cur.getColumnIndex(CalendarContract.Calendars._ID)
        val accountNameIndex = cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
        val accountTypeIndex = cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)
        val ownerAccountIndex = cur.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)
        val displayViewIndex = cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
        val visibleIndex = cur.getColumnIndex(CalendarContract.Calendars.VISIBLE)
        val isPrimaryIndex = getIsPrimaryIndex(cur)

        return CalendarEntity().apply {
            id = cur.getLong(idIndex)
            accountName = cur.getString(accountNameIndex)
            accountType = cur.getString(accountTypeIndex)
            ownerAccount = cur.getString(ownerAccountIndex)
            displayName = cur.getString(displayViewIndex)
            visible = cur.getInt(visibleIndex) == 1
            isPrimary = getIsPrimary(isPrimaryIndex, cur)
        }
    }

    private fun getIsPrimaryIndex(cur: Cursor): Int {
        val isPrimaryIndex = cur.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY)
        return if (isPrimaryIndex == -1)
            cur.getColumnIndex("COALESCE(isPrimary, ownerAccount = account_name)")
        else
            isPrimaryIndex
    }

    private fun getIsPrimary(isPrimaryIndex: Int, cur: Cursor): Boolean {
        val isPrimary = if (isPrimaryIndex == -1) 0 else cur.getInt(isPrimaryIndex)
        return isPrimary == 1
    }
}

