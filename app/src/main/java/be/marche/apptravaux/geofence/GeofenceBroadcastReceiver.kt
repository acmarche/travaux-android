package be.marche.apptravaux.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.w("zeze broadcast ")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Timber.w("zeze broad error")
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                this,
                geofenceTransition,
                triggeringGeofences
            )

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails)
            Timber.w("zeze" + geofenceTransitionDetails)
        } else {
            // Log the error.
            Timber.w("zeze " + geofenceTransition)
        }
    }

    private fun sendNotification(geofenceTransitionDetails: String) {
        Timber.w("zeze notification")
    }

    private fun getGeofenceTransitionDetails(
        geofenceBroadcastReceiver: GeofenceBroadcastReceiver,
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>
    ): String {
        Timber.i("zeze detail " + geofenceTransition + " list " + triggeringGeofences)
        return "cool"
    }


}
