package be.marche.apptravaux.screens.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.*

//android/compose/MapSampleActivity.kt
//https://github.com/googlemaps/android-maps-compose

class MapNew {

    @Composable
    fun GoogleMapView(
        modifier: Modifier,
        cameraPositionState: CameraPositionState,
        position: LatLng,
        onMapLoaded: () -> Unit,
    ) {
        val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
        val mapProperties by remember {
            mutableStateOf(
                MapProperties(
                    mapType = MapType.NORMAL
                )
            )
        }
        val mapOptions = GoogleMapOptions()
            .zoomControlsEnabled(true)
            .zoomGesturesEnabled(true)

        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            googleMapOptionsFactory = { mapOptions },
            uiSettings = uiSettings,
            onMapLoaded = onMapLoaded,
            onPOIClick = {
                Log.d("ZEZE", "POI clicked: ${it.name}")
            }
        ) {
            // Drawing on the map is accomplished with a child-based API
            val markerClick: (Marker) -> Boolean = {
                Log.d("ZEZE", "${it.title} was clicked")
                false
            }

            Marker(
                position = position,
                title = "Marker in Sydney",
                onClick = markerClick,
                draggable = true,
            )
        }

        /*    Column {
                val coroutineScope = rememberCoroutineScope()
                DebugView(cameraPositionState)
            }*/
    }

    @Composable
    private fun DebugView(cameraPositionState: CameraPositionState) {
        Column(
            Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            if (cameraPositionState.isMoving) {
                Marker(
                    position = cameraPositionState.position.target,
                    title = "Marker in Sydney",
                    //     onClick = markerClick,
                    draggable = true,
                )
            }
            Text(text = "Camera is moving")
        }
    }
}
