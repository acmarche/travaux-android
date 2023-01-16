package be.marche.apptravaux.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import be.marche.apptravaux.permission.PermissionUtil
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsAskScreen(onPermissionsOk: () -> Unit) {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        PermissionUtil.listOfPermissions
    )
    val context = LocalContext.current
    RequestPermissionUi(
        multiplePermissionsState,
        navigateToSettingsScreen = {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
            )
        },
        onPermissionsOk
    )
}

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


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionUi(
    multiplePermissionsState: MultiplePermissionsState,
    navigateToSettingsScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    when {
        // If all permissions are granted, then show screen with the feature enabled
        multiplePermissionsState.allPermissionsGranted -> {
            Button(
                onClick = navigateToHomeScreen
            )
            {
                Text("Accéder à l'application")
            }
        }
        // If the user denied any permission but a rationale should be shown, or the user sees
        // the permissions for the first time, explain why the feature is needed by the app and
        // allow the user decide if they don't want to see the rationale any more.
        multiplePermissionsState.shouldShowRationale ||
                !multiplePermissionsState.allPermissionsGranted -> {
            Column {
                val revokedPermissions = multiplePermissionsState.revokedPermissions
                PermissionsText(revokedPermissions)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(
                        onClick = {
                            multiplePermissionsState.launchMultiplePermissionRequest()
                        }
                    ) {
                        Text("Accepter les permissions")
                    }
                }
            }
        }
        // If the criteria above hasn't been met, the user denied some permission. Let's present
        // the user with a FAQ in case they want to know more and send them to the Settings screen
        // to enable them the future there if they want to.
        else -> {
            Column {
                val revokedPermissionsText = PermissionUtil.getPermissionsText(
                    multiplePermissionsState.revokedPermissions
                )
                Text(
                    "$revokedPermissionsText denied. See this FAQ with " +
                            "information about why we need this permission. Please, grant us " +
                            "access on the Settings screen."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = navigateToSettingsScreen) {
                    Text("Open Settings")
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsText(revokedPermissions: List<PermissionState>) {
    Text(
        "Il est nécessaire d'accepter les autorisations suivantes pour " +
                "utiliser l'application"
    )
    val revokedPermissionsText = PermissionUtil.getPermissionsText(
        revokedPermissions
    )
    Text(
        "$revokedPermissionsText est nécessaire. " +
                "Merci d'accepter."
    )
}
