package be.marche.apptravaux.screens.avaloir

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.SearchResponseUiState
import be.marche.apptravaux.location.GeolocationServiceViewModel
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.networking.ConnectionState
import be.marche.apptravaux.networking.connectivityState
import be.marche.apptravaux.screens.widgets.*
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.ui.theme.ScreenSizeTheme
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi

class AvaloirSearchScreen(
    private val navController: NavController,
    private val locationViewModel: GeolocationServiceViewModel
) {
    @Composable
    fun SearchMainScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        val context = LocalContext.current
        val selectedItem = remember { mutableStateOf("home") }
        locationViewModel.init(context)
        val locationEnabled = remember {
            mutableStateOf(locationViewModel.locationEnabled())
        }
        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Recherche dans un rayon de 25m"
                ) { navController.navigate(TravauxRoutes.AvaloirHomeScreen.route) }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onClickAddAvaloir(avaloirViewModel, context)
                    },
                    shape = RoundedCornerShape(50),
                    backgroundColor = Colors.Gray900
                ) {
                    Icon(Icons.Filled.Add, tint = Color.White, contentDescription = "Add")
                }
            },
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center,
            bottomBar = {
                BottomAppBar(
                    cutoutShape = RoundedCornerShape(50),
                    backgroundColor = Colors.Gray900,
                    content = {
                        BottomNavigation {
                            BottomNavigationItem(
                                selected = selectedItem.value == "home",
                                onClick = {
                                    navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
                                },
                                icon = {
                                    Icon(Icons.Filled.Home, contentDescription = "home")
                                },
                                label = { Text(text = "Home") },
                                alwaysShowLabel = false
                            )
                            BottomNavigationItem(
                                selected = selectedItem.value == "Search",
                                onClick =
                                {
                                    navController.navigate(TravauxRoutes.AvaloirListScreen.route)
                                },
                                icon = {
                                    Icon(Icons.Filled.Search, contentDescription = "search")
                                },
                                label = { Text(text = "Search") },
                                alwaysShowLabel = false
                            )
                        }
                    }
                )
            },
            content = { contentPadding ->
                Box(modifier = Modifier.padding(contentPadding)) {
                    if (locationEnabled.value)
                        HeaderSearch(avaloirViewModel, locationViewModel)
                    else {
                        Button(
                            onClick = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) },
                        ) {
                            Text(text = "Activer ma géolocalisation")
                        }
                    }
                }
            },
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    private fun HeaderSearch(
        avaloirViewModel: AvaloirViewModel,
        locationViewModel: GeolocationServiceViewModel
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            val location = remember {
                locationViewModel.userCurrentStateLatLng
            }
            LocationText(location.value)
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )
            Button(
                onClick = {
                    locationViewModel.startLocationUpdates()
                },
            ) {
                Text(text = "Rafraîchir ma géolocalisation")
            }
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )
            val connection by connectivityState()
            val isConnected = connection == ConnectionState.Available
            ContentSearch(avaloirViewModel, isConnected, location.value)
        }
    }

    @Composable
    private fun ContentSearch(
        avaloirViewModel: AvaloirViewModel,
        isConnected: Boolean,
        location: LatLng
    ) {
        if (location.latitude > 0.0) {
            if (isConnected) {
                LaunchedEffect(true) {
                    avaloirViewModel.search(location.latitude, location.longitude, "25m")
                }
            } else {
                avaloirViewModel.searchByGeoLocal(location.latitude, location.longitude, 25.00)
            }
            ResultSearch(avaloirViewModel)
        } else {
            Text(text = "Cliquez sur rafraîchir ma géolocalisation pour lancer une recherche")
        }
    }

    @Composable
    private fun ResultSearch(
        avaloirViewModel: AvaloirViewModel
    ) {
        DescriptionText()
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
        when (val state = avaloirViewModel.resultSearch.collectAsState().value) {
            is SearchResponseUiState.Loading -> {
                Text(text = "Recherche en cours...")
                CircularProgressIndicatorSample()
            }
            is SearchResponseUiState.Error -> {
                ErrorDialog(state.message)
            }
            is SearchResponseUiState.Loaded -> {
                Text(
                    text = "${state.response.avaloirs.count()} avaloir(s) trouvé(s) dans un rayon de 25m",
                    fontSize = ScreenSizeTheme.textStyle.fontWidth_1
                )
                Divider(
                    modifier = Modifier.height(MEDIUM_PADDING),
                    color = MaterialTheme.colors.background
                )
                val widget = AvaloirWidget()
                widget.LoadAvaloirs(state.response.avaloirs, null, navController)
            }
            else -> {

            }
        }
    }

    @Composable
    private fun LocationText(location: LatLng) {
        Text(
            text = "Votre localisation: ${location.latitude}, ${location.longitude}",
            fontSize = ScreenSizeTheme.textStyle.fontWidth_1
        )
    }

    @Composable
    private fun DescriptionText() {
        Text(
            text = "Cliquez sur un avaloir trouvé ou ajouter un autre",
            fontSize = ScreenSizeTheme.textStyle.fontWidth_1,
            modifier = Modifier.padding(5.dp)
        )
    }

    @Composable
    private fun LoadScreen() {
        Text(
            text = "Recherche en cours...",
            fontSize = ScreenSizeTheme.textStyle.fontWidth_1
        )
        CircularProgressIndicator(progress = 0.5f)
    }

    private fun onClickAddAvaloir(
        avaloirViewModel: AvaloirViewModel,
        context: Context
    ) {
        avaloirViewModel.currentLatLng =
            locationViewModel.userCurrentStateLatLng.value

        val location = avaloirViewModel.currentLatLng
        if (location.latitude > 0.0) {
            navController.navigate(TravauxRoutes.AvaloirPhotoScreen.route)
        } else {
            Toast.makeText(
                context,
                "Géolocalisation invalide",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}