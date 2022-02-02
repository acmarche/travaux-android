//package me.pavi2410.vrcc
package be.marche.apptravaux.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import be.marche.apptravaux.entities.DetailModel
import be.marche.apptravaux.entities.Results
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.Icons

/**
 * interessant use remember
 */

class PhoneInfo(context: Context) {

}

@Composable
fun computeCompatibilityResults(): Results {

    val context = LocalContext.current
    val phoneInfo = remember { PhoneInfo(context) }

    /** COMPATIBILITY CRITERIA
     * ------------------------
     * Accelerometer = required
     * Gyro = required
     * Screen Size >= 5"
     * Android >= Lollipop (API 21) == Min SDK version
     * RAM >= 2 GB
     */
    val isCompatible = remember(phoneInfo) {
        with(phoneInfo) {
            true
        }
    }

    val details = remember(phoneInfo) {
        with(phoneInfo) {
            listOf(
                DetailModel(
                    icon = Icons.Accelerometer,
                    iconColor = Colors.Indigo500,
                    name = "Accelerometer",
                    result = true
                ),
                DetailModel(
                    icon = Icons.Compass,
                    iconColor = Colors.Red500,
                    name = "Compass",
                    result = true
                ),
                DetailModel(
                    icon = Icons.Gyroscope,
                    iconColor = Colors.Yellow500,
                    name = "Gyroscope",
                    result = true
                ),
            )
        }
    }

    return remember(phoneInfo) { Results(isCompatible, details) }
}

private inline val Int.GB get() = this * 1e9