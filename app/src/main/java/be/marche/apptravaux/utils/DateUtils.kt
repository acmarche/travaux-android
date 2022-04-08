package be.marche.apptravaux.utils

import android.icu.text.DateFormat
import java.time.LocalDateTime
import java.util.*

class DateUtils {

    companion object {

        const val PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

        fun formatDate(createdAt: LocalDateTime): String {
            //val format = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val t =
                DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_DAY + DateFormat.HOUR24_MINUTE)
                    .format(createdAt)

            return t
        }

        fun formatDate(createdAt: Date): String {
            //val format = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val t =
                DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_DAY + DateFormat.HOUR24_MINUTE)
                    .format(createdAt)

            return t
        }
    }

}