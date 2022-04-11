package be.marche.apptravaux.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateUtils {

    companion object {

        const val PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

        fun dateToday(withTime: Boolean = false): String {
            val date: LocalDateTime = LocalDateTime.now()
            var pattern = "yyyy-MM-dd"
            if (withTime == true) {
                pattern = "$pattern HH:mm"
            }
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
            return date.format(formatter)
        }

        fun formatDateTime(createdAt: String): String {
            val formatOut = "dd-MM-yyyy"
            val formatIn = "yyyy-MM-dd HH:mm"

            val formatterIn: DateTimeFormatter = DateTimeFormatter.ofPattern(formatIn)

            try {
                val dateTime: LocalDateTime = LocalDateTime.parse(createdAt, formatterIn)
                val formatterOut = DateTimeFormatter.ofPattern(formatOut)
                return dateTime.format(formatterOut)
            } catch (e: Exception) {

            }
            return ""
        }

        fun formatDate(createdAt: String): String {
            val formatOut = "dd-MM-yyyy"
            val formatIn = "yyyy-MM-dd"

            val formatterIn: DateTimeFormatter = DateTimeFormatter.ofPattern(formatIn)
            try {
                val dateTime: LocalDate = LocalDate.parse(createdAt, formatterIn)
                val formatterOut = DateTimeFormatter.ofPattern(formatOut)
                return dateTime.format(formatterOut)

            } catch (e: Exception) {

            }
            return ""
        }

    }

}