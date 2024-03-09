package com.chebuso.chargetimer.notifications.application

import android.os.Parcel
import android.os.Parcelable

data class AlarmItem(
    val triggerAtMillis: Long,
    val chargedAtMillis: Long,
    val notificationId: Int,
    val notificationChannelId: String?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(triggerAtMillis)
        parcel.writeLong(chargedAtMillis)
        parcel.writeInt(notificationId)
        parcel.writeString(notificationChannelId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlarmItem> {
        override fun createFromParcel(parcel: Parcel): AlarmItem {
            return AlarmItem(parcel)
        }

        override fun newArray(size: Int): Array<AlarmItem?> {
            return arrayOfNulls(size)
        }
    }
}