package be.marche.apptravaux.screens.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap

//android/compose/MapSampleActivity.kt
//https://github.com/googlemaps/android-maps-compose
@Composable
fun MapNew() {
    val singapore = LatLng(1.35, 103.87)
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        googleMapOptionsFactory = {
            GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(singapore, 10f))
        }
    )
}

@Composable
private fun MapButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary
        ),
        onClick = onClick
    ) {
        Text(text = text, style = MaterialTheme.typography.h5)
    }
}