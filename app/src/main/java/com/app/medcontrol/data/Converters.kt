package com.app.medcontrol.data

import androidx.room.TypeConverter
import com.app.medcontrol.data.entity.StatusConsumo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalTimeList(value: List<LocalTime>?): String? {
        return value?.joinToString(",") { it.toString() }
    }

    @TypeConverter
    fun toLocalTimeList(value: String?): List<LocalTime>? {

        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").map { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromStatus(value: StatusConsumo): String = value.name

    @TypeConverter
    fun toStatus(value: String): StatusConsumo = StatusConsumo.valueOf(value)

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }
}