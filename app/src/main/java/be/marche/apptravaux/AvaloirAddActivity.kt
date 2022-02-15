package be.marche.apptravaux

import android.content.*
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import be.marche.apptravaux.location.CurrentLocationService
import be.marche.apptravaux.location.LocationService
import be.marche.apptravaux.location.SharedPreferenceUtil
import be.marche.apptravaux.screens.avaloir.AvaloirAddScreen
import be.marche.apptravaux.viewModel.AvaloirViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvaloirAddActivity : ComponentActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var foregroundOnlyLocationServiceBound = false
    private var currentOnlyLocationService: CurrentLocationService? = null
    private lateinit var foregroundOnlyBroadcastReceiver: AvaloirAddActivity.ForegroundOnlyBroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences
    private val foregroundOnlyServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as CurrentLocationService.LocalBinder
            currentOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
            currentOnlyLocationService?.subscribeToLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            currentOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    object MyBitmap {
        var bitmap: Bitmap? = null
    }

    val locationService = LocationService()
    private val avaloirViewModel: AvaloirViewModel by viewModels()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  syncContent()

        Log.d("ZEZE", "Add activity create")
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val enabled = sharedPreferences.getBoolean(
            SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false
        )
        if (enabled) {
            currentOnlyLocationService?.unSubscribeToLocationUpdates()
        } else {
            if (locationService.foregroundPermissionApproved(this)) {
                currentOnlyLocationService?.subscribeToLocationUpdates()
                    ?: Log.d("TAG", "Service Not Bound")
            } else {
                locationService.requestForegroundPermissions(this)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED) {
            updateButtonState(
                sharedPreferences.getBoolean(
                    SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false
                )
            )
        }
    }

    private fun updateButtonState(trackingLocation: Boolean) {
        //Update the location here #trackingLocation
    }

    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {
        @OptIn(ExperimentalMaterialApi::class)
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                CurrentLocationService.EXTRA_LOCATION
            )
            Log.d("ZEZE", "Add activity ForegroundOnlyBroadcastReceiver")
            if (location != null) {
                locationService.locations = location
                Log.d("ZEZE", "ma location {$location}")
                setContent {
                    val t =
                        AvaloirAddScreen(avaloirViewModel = avaloirViewModel)
                    t.LocationScreen(location)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        updateButtonState(
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        )
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, CurrentLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                CurrentLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (currentOnlyLocationService != null) {
            currentOnlyLocationService?.unSubscribeToLocationUpdates()
        }

        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onStop()
    }
}
