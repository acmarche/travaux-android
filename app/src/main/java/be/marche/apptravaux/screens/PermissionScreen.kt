package be.marche.apptravaux.screens

import android.Manifest
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(onPermissionsOk: () -> Unit, onPermissionsKo: () -> Unit) {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            // android.Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    )
    Log.d("ZEZE", "launch permissions screen")
    LaunchedEffect(multiplePermissionsState) {
        when {
            // If all permissions are granted, then show screen with the feature enabled
            multiplePermissionsState.allPermissionsGranted -> {
                Log.d("ZEZE", "allPermissionsGranted {$multiplePermissionsState}")
                onPermissionsOk()
            }
            else -> {
                Log.d("ZEZE", "allPermissionsNotGranted ")
                onPermissionsKo()
            }
        }
    }
}
