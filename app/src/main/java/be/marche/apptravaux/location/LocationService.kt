package be.marche.apptravaux.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.model.LatLng

class LocationService {

    var locations: Location? = null
    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

    fun foregroundPermissionApproved(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun requestForegroundPermissions(activity: Activity) {
        val provideRationale = foregroundPermissionApproved(activity.applicationContext)

        if (provideRationale) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    fun getDeviceLocation(
        context: Context,
        viewModel: AvaloirViewModel
    ) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            if (viewModel.locationPermissionGranted.value == true) {
                val locationResult = fusedLocationProviderClient.lastLocation

                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val lastKnownLocation = task.result

                        if (lastKnownLocation != null) {
                            Log.e("ZEZE", "location service ${lastKnownLocation.latitude}")
                            viewModel.userCurrentLatLng.value =
                                LatLng(
                                    lastKnownLocation.latitude,
                                    lastKnownLocation.longitude
                                )
                        }
                    } else {
                        Log.d("ZEZE", " Current User location is null")
                    }
                }

            }

        } catch (e: SecurityException) {
            Log.d("Exception", "Exception:  $e.message.toString()")
        }
    }
}