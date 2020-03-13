package be.marche.apptravaux.location

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationLiveJf(context: Context) : LiveData<LocationData>() {

    private var firstSubscriber: Boolean = true
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onActive() {
        super.onActive()
        if (firstSubscriber) {
            requestLastLocation()
            requestLocation()
            firstSubscriber = false
        }
    }

    private fun requestLocation() {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            value = LocationData(exception = e)
        }
    }

    private fun requestLastLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { lastLocation ->
                    value = LocationData(location = lastLocation)
                }

            fusedLocationClient.lastLocation
                .addOnFailureListener { exception ->
                    value = LocationData(exception = exception)
                }
        } catch (e: SecurityException) {
            value = LocationData(exception = e)
        }
    }

    private fun setLocationData(location: Location) {
        value = LocationData(location = location)
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }
}