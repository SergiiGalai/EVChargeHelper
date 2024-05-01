package com.chebuso.chargetimer.shared.helpers

fun String.trimNonPrintable() = this.trim { it <= ' ' }
fun String?.emptyIfNull(): String = this ?: ""
fun String.toFallbackInt(default: Int = 0) = if ("" == this) default else this.toInt()
fun String.toFallbackLong(default: Long = 0) = if ("" == this) default else this.toLong()
