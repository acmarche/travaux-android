package be.marche.apptravaux.samples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import be.marche.apptravaux.R
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTravaux6Theme {
                Surface(color = MaterialTheme.colors.background) {
                    GoogleMap2()
                }
            }
        }
    }
}

@Composable
fun GoogleMap2() {
    val mapView = rememberMapViewWithLifeCycle2()

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
                    val pickUp = LatLng(50.2266, 5.3429) //Delhi
                    val destination = LatLng(50.2350, 5.3591) //Bangalore
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 13f))
                    val markerOptions = MarkerOptions()
                        .title("Delhi")
                        .position(pickUp)
                    map.addMarker(markerOptions)
                    val markerOptionsDestination = MarkerOptions()
                        .title("Bangalore")
                        .position(destination)
                    map.addMarker(markerOptionsDestination)

                    map.addPolyline(
                        PolylineOptions().add(
                            pickUp,
                            LatLng(50.2266, 5.3429), //Root of Gujarat
                            LatLng(50.2350, 5.3591), //Root of Maharashtra
                            destination
                        )
                    ).color = R.color.purple_500 //Polyline color
                }
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifeCycle2(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            //id = com.google.maps.android.ktx.R.id.map_frame
        }
    }
    val lifeCycleObserver = rememberMapLifecycleObserver2(mapView)
    val lifeCycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifeCycle) {
        lifeCycle.addObserver(lifeCycleObserver)
        onDispose {
            lifeCycle.removeObserver(lifeCycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver2(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }
