package be.marche.apptravaux.geofence

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber

class GeofenceIntentService : IntentService("TravauxGeofenceIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent)
        if (geofenceEvent.hasError()) {
            Timber.w("zeze intent event error" + geofenceEvent.errorCode)
            return
        }
        val geofenceTransition = geofenceEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Timber.w("zeze intent event not supported" + Geofence.GEOFENCE_TRANSITION_DWELL)
            return
        }
        if (geofenceEvent.triggeringGeofences == null) {
            Timber.w("zeze intent event vident")
            return
        }
        for (triggeringGeofence in geofenceEvent.triggeringGeofences) {
            Timber.w("zeze event found: %s", triggeringGeofence.requestId)
            if (triggeringGeofence.requestId == "mageofence") {

            }
        }
    }
}