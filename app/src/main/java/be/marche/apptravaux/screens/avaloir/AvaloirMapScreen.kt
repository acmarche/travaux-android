package be.marche.apptravaux.screens.avaloir

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.MapJf
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng as LatLngGms

class AvaloirMapScreen(
    val navController: NavController,
) {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ScreenMain(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBarJf(
                    stringResource(R.string.add_avaloir)
                ) { navController.navigate(TravauxRoutes.AvaloirHomeScreen.route) }
            }
        ) {
            ContentMainScreen(avaloirViewModel)
        }
    }

    @Composable
    private fun ContentMainScreen(
        avaloirViewModel: AvaloirViewModel
    ) {
        val location = remember {
            mutableStateOf(avaloirViewModel.currentLatLng)
        }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 25.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Déplacer la carte pour corriger la localisation",
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LocationText(location = location.value)
                Divider(
                    modifier = Modifier.height(MEDIUM_PADDING),
                    color = MaterialTheme.colors.background
                )
                Button(onClick = { navController.navigate(TravauxRoutes.AvaloirPhotoScreen.route) }) {
                    Text("Valider et prendre une photo")
                }
                Button(onClick = {
                    navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
                }
                ) {
                    Text("Annuler")
                }
            }
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )

            val singapore = LatLngGms(location.value.latitude, location.value.longitude)
            var isMapLoaded by remember { mutableStateOf(false) }
            // Observing and controlling the camera's state can be done with a CameraPositionState
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(singapore, 18f)
            }

            val map = MapJf(location) {}

            Box(Modifier.fillMaxSize()) {

                map.GoogleMapWidget(
                    location.value.latitude,
                    location.value.longitude,
                    null,
                    true
                )

                /*    map.GoogleMapView(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = {
                            isMapLoaded = true
                        },
                    )*/

                isMapLoaded = true
                if (!isMapLoaded) {
                    androidx.compose.animation.AnimatedVisibility(
                        modifier = Modifier
                            .matchParentSize(),
                        visible = !isMapLoaded,
                        enter = EnterTransition.None,
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .wrapContentSize()
                        )
                    }
                }
            }


        }

    }

    @Composable
    private fun LocationText(location: LatLng?) {
        if (location != null) {
            Text(text = "Votre localisation: ${location.latitude}, ${location.longitude}")
        } else {
            Text(text = "Pas de localisation")
        }
    }
}