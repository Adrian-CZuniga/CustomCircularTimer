package com.example.customprogresstimer

import android.util.Log
import java.util.Locale

internal fun Long.toTimeString(timeFormat: TimeFormat, locale: Locale = Locale.getDefault()): String {
    return when (timeFormat) {
        TimeFormat.HH_MM -> {
            val hours = (this / 3600000)
            val minutes = (this / 60000) % 60
            String.format(locale, "%02dh:%02dm", hours, minutes)
        }
        TimeFormat.MM_SS -> {
            val minutes = (this / 60000)
            val seconds = (this / 1000) % 60
            String.format(locale, "%02dm:%02ds", minutes, seconds )
        }
        TimeFormat.SS_MSMS -> {
            val seconds = (this / 1000)
            val milliseconds = (this / 10) % 100
            String.format(locale, "%02ds:%02dms", seconds, milliseconds)
        }
        else -> throw IllegalArgumentException("Unknown time format")
    }
}