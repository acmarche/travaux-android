package be.marche.apptravaux.viewModel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class LocationViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private lateinit var locationCallback2: LocationCallback
    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

    var currentLatLng: LatLng = LatLng(0.0, 0.0)
    private var _userCurrentStateLatLng = mutableStateOf(LatLng(0.0, 0.0))
    var userCurrentStateLatLng: MutableState<LatLng> = _userCurrentStateLatLng

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)

    fun start() {
        if (locationEnabled()) {
            getLast()
            startLocationUpdates()
        }
    }

    companion object {

        private val UPDATE_INTERVAL = 10 * 1000 /* 10 secs */.toLong()
        private val FASTEST_INTERVAL: Long = 25000 /* 25 sec */

        val locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                _userCurrentStateLatLng.value = LatLng(location.latitude, location.longitude)
            }
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestForegroundPermissions(applicationContext)
            return
        }
    }

    private fun getLast() {
        checkPermission()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.also {
                    _userCurrentStateLatLng.value = LatLng(location.latitude, location.longitude)
                }
            }
    }

    private fun startLocationUpdates() {
        checkPermission()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
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

    fun foregroundPermissionApproved(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun locationEnabled(): Boolean {
        val locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun stopLocation() {
        try {
            val removeTask = fusedLocationClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                } else {
                }
            }
        } catch (e: Exception) {
        }
    }


    /**
     * LOCATION
     */
    private var _locationPermissionGranted = MutableLiveData(true)
    var locationPermissionGranted: LiveData<Boolean> = _locationPermissionGranted
}
