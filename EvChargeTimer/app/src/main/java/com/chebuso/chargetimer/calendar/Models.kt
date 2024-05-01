package com.chebuso.chargetimer.calendar

import com.chebuso.chargetimer.shared.helpers.emptyIfNull

class CalendarEntity {
    var id: Long = 0
    var displayName: String? = null
    var accountName: String? = null
    var accountType: String? = null
    var ownerAccount: String? = null
    var isPrimary = false
    var visible = false

    val isPrimaryAlternative: Boolean
        get() = accountName.emptyIfNull() == ownerAccount.emptyIfNull()
}

enum class CalendarEventColor(val value: Int) {
    BLUE(1), GREEN(2), VIOLET(3), SLIGHTLY_RED(4),
    YELLOW(5), RED(6), BLUE_STANDARD(7)
}

data class CalendarEventEntity(
    val title: String,
    val description: String,
    val millisToStart: Long,
    val eventColor: CalendarEventColor = CalendarEventColor.VIOLET,
)