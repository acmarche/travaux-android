package be.marche.apptravaux.location

import android.content.Context
import android.location.Location
import androidx.core.content.edit
import be.marche.apptravaux.R

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

internal object SharedPreferenceUtil {
    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"
    fun getLocationTrackingPref(context: Context): Boolean =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).getBoolean(KEY_FOREGROUND_ENABLED, false)
    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit {
                putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        }
}






















