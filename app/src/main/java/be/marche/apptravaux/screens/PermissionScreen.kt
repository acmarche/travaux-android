package be.marche.apptravaux.screens

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(navController: NavController) {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            // android.Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    )
    LaunchedEffect(multiplePermissionsState) {
        when {
            // If all permissions are granted, then show screen with the feature enabled
            multiplePermissionsState.allPermissionsGranted -> {
                Log.d("ZEZE", "allPermissionsGranted {$multiplePermissionsState}")
                navController.navigate(TravauxScreens.HomeScreen.route)
            }
            else -> {
                Log.d("ZEZE", "allPermissionsNotGranted ")
                val intent = Intent(navController.context, PermissionActivity::class.java)
                ContextCompat.startActivity(navController.context, intent, null)
            }
        }
    }
}
