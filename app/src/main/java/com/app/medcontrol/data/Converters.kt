package com.app.medcontrol.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalTime

class Converters {
    private val gson = Gson()


    @TypeConverter
    fun fromLocalTimeList(value: List<LocalTime>?): String? {
        return gson.toJson(value)
    }


    @TypeConverter
    fun toLocalTimeList(value: String?): List<LocalTime>? {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<LocalTime>>() {}.type
        return gson.fromJson(value, listType)
    }
}