package com.app.medcontrol.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalTimeList(value: List<LocalTime>?): String? {
        return value?.joinToString(",") { it.toString() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalTimeList(value: String?): List<LocalTime>? {

        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").map { LocalTime.parse(it) }
    }
}