package com.example.criminalintent

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(value: Long): Date = Date(value)

    @TypeConverter
    fun fromUUID(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun toUUID(value: String): UUID =
        UUID.fromString(value)
}
