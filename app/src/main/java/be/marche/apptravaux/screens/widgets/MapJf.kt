package be.marche.apptravaux.screens.widgets

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.GoogleMapOptions
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MapJf(
    val positionState: MutableState<LatLng>,
    var onMapLoaded: () -> Unit
) {

    //init
    var marker: Marker? = null

    @Composable
    fun GoogleMapWidget(
        latitude: Double,
        longitude: Double,
        name: String?,
        move: Boolean,
    ) {
        val mapView = rememberMapViewWithLifeCycle(move)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AndroidView(
                { mapView }
            ) { mapView ->
                CoroutineScope(Dispatchers.Main).launch {
                    val map = mapView.getMapAsync { map ->
                        map.uiSettings.isZoomControlsEnabled = true
                        val latLng = LatLng(latitude, longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                        addMarker(map, name, latLng)
                        /*val markerOption = MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .position(latLng)
                    marker = map.addMarker(markerOption)*/
                    }
                }
            }
        }
    }

    @Composable
    fun rememberMapViewWithLifeCycle(move: Boolean): MapView {
        val mapOptions = GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NORMAL)
            .zoomControlsEnabled(true)
            .zoomGesturesEnabled(true)
        val context = LocalContext.current
        val mapView = remember {
            MapView(context, mapOptions).apply {
                //id = com.google.maps.android.ktx.R.id.map_frame
            }
        }

        if (move) {
            mapView.getMapAsync { map ->
                map.setOnCameraMoveListener {
                    val cameraPosition: CameraPosition = map.cameraPosition
                    moveMarker(cameraPosition.target.latitude, cameraPosition.target.longitude)
                }
                map.setOnCameraIdleListener {
                    val cameraPosition: CameraPosition = map.cameraPosition
                    positionState.value =
                        LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude)
                }
            }
        }

        val lifeCycleObserver = rememberMapLifecycleObserver(mapView)
        val lifeCycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifeCycle) {
            lifeCycle.addObserver(lifeCycleObserver)
            onDispose {
                lifeCycle.removeObserver(lifeCycleObserver)
            }
        }

        return mapView
    }

    private fun addMarker(map: GoogleMap, name: String?, latLng: LatLng) {
        val markerOption = MarkerOptions()
            .position(latLng)
            .title(name)
            .position(latLng)
        marker = map.addMarker(markerOption)
    }

    private fun moveMarker(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        marker?.position = latLng
    }

    @Composable
    fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
        remember(mapView) {
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        mapView.onCreate(Bundle())
                        //    onMapLoaded
                    }
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                    else -> throw IllegalStateException()
                }
            }
        }
}