package be.marche.apptravaux.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import be.marche.apptravaux.avaloir.entity.Avaloir
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import timber.log.Timber

class GeofenceManager(context: Context) {
    //on recurpe TravauxApp
    private val appContext = context.applicationContext
    private val geofencingClient = LocationServices.getGeofencingClient(appContext)
    private val geofenceList = mutableListOf<Geofence>()

    fun addGeofenceToList(
        latitude: Double,
        longitude: Double,
        requestId: String,
        radiusMeter: Float = 1000.0f
    ) {
        Timber.w("zeze add geofence " + requestId)
        geofenceList.add(
            Geofence.Builder()
                .setRequestId(requestId)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(
                    latitude,
                    longitude,
                    radiusMeter
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        )
    }

    fun createGeoFence() {
        Timber.w("zeze create geofence")

        val task = geofencingClient.addGeofences(geoFencingRequest(), geofencePendingIntent)
        task.addOnSuccessListener {
            Timber.w("zeze pending succes")
        }
        task.addOnFailureListener {
            Timber.w("zeze pending error: " + it.message)
        }
    }

    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(appContext, GeofenceIntentService::class.java)
        PendingIntent.getService(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun geoFencingRequest(): GeofencingRequest? {
        Timber.w("zeze list size " + geofenceList.size)
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)//si on est a l'interieur on veut declenche
            .addGeofences(geofenceList)
            .build()
    }

    fun removeAllGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent)
        geofenceList.clear()
    }


}