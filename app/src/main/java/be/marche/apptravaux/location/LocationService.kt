package be.marche.apptravaux.location

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.libraries.maps.model.LatLng
import timber.log.Timber

class LocationService : Service() {

    var currentLocationState: MutableState<LatLng> = mutableStateOf(LatLng(00.00, 0.00))

    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    private val UPDATE_INTERVAL = 10 * 1000 /* 10 secs */.toLong()
    private val FASTEST_INTERVAL: Long = 25000 /* 25 sec */
    private val localBinder = LocalBinder()

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    fun foregroundPermissionApproved(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun requestForegroundPermissions(activity: Context) {
        val provideRationale = foregroundPermissionApproved(activity.applicationContext)

        if (provideRationale) {
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    fun getDeviceLocation(
        context: Context
    ) {
        Timber.d("start get device ${context.applicationContext}")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    currentLocationState.value = convertLocationToLng(location)
                    Timber.d("on result $currentLocationState")
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestForegroundPermissions(context)
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener {
                currentLocationState.value = convertLocationToLng(it)
                Timber.d("location service success ${currentLocationState}")
            }
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val lastKnownLocation = task.result
                    if (lastKnownLocation != null && lastKnownLocation.latitude > 0.0) {
                        currentLocationState.value = convertLocationToLng(lastKnownLocation)
                        Timber.d(
                            "location service on complete ${currentLocationState}"
                        )
                    }
                } else {
                    Timber.d(" Current User location is null")
                }
            }
        } catch (e: SecurityException) {
            Timber.d("Exception:  $e.message.toString()")
        }
    }

    private fun convertLocationToLng(location: Location?): LatLng {
        if (location != null)
            return LatLng(location.latitude, location.longitude)
        else
            return LatLng(0.0, 0.0)
    }

    fun stopLocation() {
        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("ok to remove Location callback")
                    stopSelf()
                } else {
                    Timber.d("Failed to remove Location callback")
                }
            }
            Timber.d("location stoped")
        } catch (e: Exception) {
            Timber.d("Failed to remove Location callback ${e.message}")
        }
    }

    fun locationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onBind(p0: Intent?): IBinder {
        stopForeground(true)
        return localBinder
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService get() = this@LocationService
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("destroy service")
    }


}