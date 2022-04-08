package be.marche.apptravaux.database

import androidx.room.TypeConverter
import be.marche.apptravaux.utils.DateUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTime(value: String?): LocalDateTime? {
        val pattern = DateTimeFormatter.ofPattern(DateUtils.PATTERN)
        return LocalDateTime.now()
    }

    @TypeConverter
    fun dateToTime(date: LocalDateTime?): String? {
        val pattern = DateTimeFormatter.ofPattern(DateUtils.PATTERN)
        return date?.toString()
    }

}