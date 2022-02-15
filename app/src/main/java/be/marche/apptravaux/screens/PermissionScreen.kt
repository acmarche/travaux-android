package be.marche.apptravaux.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import be.marche.apptravaux.permission.PermissionUtil
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(onPermissionsOk: () -> Unit, onPermissionsKo: () -> Unit) {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        PermissionUtil.listOfPermissions
    )
    LaunchedEffect(multiplePermissionsState) {
        when {
            // If all permissions are granted, then show screen with the feature enabled
            multiplePermissionsState.allPermissionsGranted -> {
                onPermissionsOk()
            }
            else -> {
                onPermissionsKo()
            }
        }
    }
}
