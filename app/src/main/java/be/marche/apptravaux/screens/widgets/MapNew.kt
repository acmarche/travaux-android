package be.marche.apptravaux.screens.widgets

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.*

//android/compose/MapSampleActivity.kt
//https://github.com/googlemaps/android-maps-compose

class MapNew {

    val singapore2 = LatLng(50.22, 5.34)

    @Composable
    fun GoogleMapView(
        modifier: Modifier,
        cameraPositionState: CameraPositionState,
        onMapLoaded: () -> Unit,
    ) {
        Log.d("ZEZE", "create GoogleMapView")
        var uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
        var shouldAnimateZoom by remember { mutableStateOf(true) }
        var ticker by remember { mutableStateOf(0) }
        var mapProperties by remember {
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

            val dragState: MarkerDragState = MarkerDragState()

            val marker =
                Marker(
                    position = singapore2,
                    title = "Marker in Sydney",
                    onClick = markerClick,
                    draggable = true,
                    markerDragState = dragState
                )
        }

        Column {
            val coroutineScope = rememberCoroutineScope()
            DebugView(cameraPositionState)
        }
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
