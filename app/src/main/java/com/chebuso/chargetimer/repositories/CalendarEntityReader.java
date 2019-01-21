package com.chebuso.chargetimer.repositories;

import android.database.Cursor;
import android.provider.CalendarContract;

import com.chebuso.chargetimer.models.CalendarEntity;

class CalendarEntityReader {
    CalendarEntity fromCursorPosition(Cursor cur){
        int idIndex = cur.getColumnIndex(CalendarContract.Calendars._ID);
        int accountNameIndex = cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME);
        int accountTypeIndex = cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE);
        int ownerAccountIndex = cur.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT);
        int displayViewIndex = cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
        int visibleIndex = cur.getColumnIndex(CalendarContract.Calendars.VISIBLE);
        int isPrimaryIndex = cur.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY);
        if (isPrimaryIndex == -1) {
            isPrimaryIndex = cur.getColumnIndex("COALESCE(isPrimary, ownerAccount = account_name)");
        }

        CalendarEntity result = new CalendarEntity();

        result.id = cur.getLong(idIndex);
        result.accountName = cur.getString(accountNameIndex);
        result.accountType = cur.getString(accountTypeIndex);
        result.ownerAccount = cur.getString(ownerAccountIndex);
        result.displayName = cur.getString(displayViewIndex);
        result.visible = cur.getInt(visibleIndex) == 1;

        int isPrimary = isPrimaryIndex== -1 ? -1 : cur.getInt(isPrimaryIndex);
        if (isPrimary == -1 && result.accountName.equals(result.ownerAccount))
            isPrimary = 1;

        result.isPrimary = isPrimary == 1;
        return result;
    }
}
