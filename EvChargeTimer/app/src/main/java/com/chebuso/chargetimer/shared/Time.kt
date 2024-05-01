package com.chebuso.chargetimer.shared

data class Time (val days: Int, val hours: Int, val minutes: Int)
{
    constructor(hours: Int, minutes: Int): this(0, hours, minutes)
}