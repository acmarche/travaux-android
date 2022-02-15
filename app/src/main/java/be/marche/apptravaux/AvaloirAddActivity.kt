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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import be.marche.apptravaux.location.CurrentLocationService
import be.marche.apptravaux.location.LocationService
import be.marche.apptravaux.screens.avaloir.AvaloirAddScreen
import be.marche.apptravaux.viewModel.AvaloirViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvaloirAddActivity : ComponentActivity() {

    private var foregroundOnlyLocationServiceBound = false
    private var currentOnlyLocationService: CurrentLocationService? = null
    private lateinit var foregroundOnlyBroadcastReceiver: AvaloirAddActivity.ForegroundOnlyBroadcastReceiver

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

        Log.d("ZEZE", "Add activity create")
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        currentOnlyLocationService?.unSubscribeToLocationUpdates()
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
        Log.d("ZEZE", "Add activity start")
        val serviceIntent = Intent(this, CurrentLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        Log.d("ZEZE", "Add activity resume")
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                CurrentLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    override fun onPause() {
        Log.d("ZEZE", "Add activity pause")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        Log.d("ZEZE", "Add activity stop")
        if (currentOnlyLocationService != null) {
            currentOnlyLocationService?.unSubscribeToLocationUpdates()
        }

        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }

        super.onStop()
    }
}
