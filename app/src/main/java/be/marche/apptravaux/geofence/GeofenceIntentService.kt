package be.marche.apptravaux.geofence

import android.app.IntentService
import android.content.Intent
import be.marche.apptravaux.avaloir.RedirectActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber

class GeofenceIntentService : IntentService("TravauxGeofenceIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent)
        if (geofenceEvent.hasError()) {
            return
        }
        val geofenceTransition = geofenceEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            return
        }
        if (geofenceEvent.triggeringGeofences == null) {
            return
        }
        for (triggeringGeofence in geofenceEvent.triggeringGeofences) {
            if (triggeringGeofence.requestId == "mageofence") {
                goTo(geofenceTransition, triggeringGeofence.requestId)
            }
        }
    }

    private fun goTo(geofenceTransition: Int, idAvaloir: String) {
        val intent = Intent(this, RedirectActivity::class.java).apply {
            putExtra("id", idAvaloir)
        }
        startActivity(intent)
    }
}