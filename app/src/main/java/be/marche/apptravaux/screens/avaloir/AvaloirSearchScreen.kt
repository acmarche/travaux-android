package be.marche.apptravaux.screens.avaloir

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.networking.ConnectionState
import be.marche.apptravaux.networking.connectivityState
import be.marche.apptravaux.screens.widgets.*
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.viewModel.LocationViewModel
import com.google.android.libraries.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi


class AvaloirSearchScreen(
    private val navController: NavController,
    private val locationViewModel: LocationViewModel
) {

    @Composable
    fun SearchMainScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        val selectedItem = remember { mutableStateOf("home") }
        val context = LocalContext.current
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
                        onClickAddButton(avaloirViewModel, context)
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
                                    locationViewModel.stopLocation()
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
                                    locationViewModel.stopLocation()
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
            content = {
                if (locationEnabled.value)
                    BeginSearch(avaloirViewModel)
                else {
                    Button(
                        onClick = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) },
                    ) {
                        Text(text = "Activer ma géolocalisation") //todo how on change value result
                    }
                }
            },
        )
    }

    @Composable
    private fun BeginSearch(
        avaloirViewModel: AvaloirViewModel,
    ) {
        val context = LocalContext.current
        SideEffect {
            //locationService.getDeviceLocation(context)
        }
        val location = remember {
            locationViewModel.userCurrentStateLatLng
        }
        Column(modifier = Modifier.padding(15.dp)) {
            ContentSearch1(avaloirViewModel, location.value)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    private fun ContentSearch1(
        avaloirViewModel: AvaloirViewModel,
        location: LatLng
    ) {
        LocationText(location)
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
        DescriptionText()
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
        val connection by connectivityState()
        val isConnected = connection == ConnectionState.Available

        ContentSearch2(avaloirViewModel, isConnected, location)
    }

    @Composable
    private fun ContentSearch2(
        avaloirViewModel: AvaloirViewModel,
        isConnected: Boolean,
        location: LatLng
    ) {
        if (isConnected) {
            if (location.latitude > 0.0) {
                LaunchedEffect(true) {
                    avaloirViewModel.search(location.latitude, location.longitude, "25m")
                }
                ResultSearch(avaloirViewModel)
            }
        } else {
            CardRow(texte = "La recherche par géolocalisation ne peut se faire que si internet est fonctionnel") {

            }
            Text(text = "Pas de géolocalisation !!")
            Button(
                onClick = {
                    locationViewModel.start()
                },
            ) {
                Text(text = "Rafraichir ma géolocalisation")
            }
        }
    }

    @Composable
    private fun ResultSearch(
        avaloirViewModel: AvaloirViewModel
    ) {
        when (val state = avaloirViewModel.resultSearch.collectAsState().value) {
            is SearchResponseUiState.Loading -> {
                //LoadScreen()
                Text(text = "Recherche en cours...")
                CircularProgressIndicatorSample()
            }
            is SearchResponseUiState.Error -> {
                ErrorDialog(state.message)
            }
            is SearchResponseUiState.Loaded -> {
                Text("${state.response.avaloirs.count()} avaloir(s) trouvé(s) dans un rayon de 25m")
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
        Text(text = "Votre localisation: ${location.latitude}, ${location.longitude}")
    }

    @Composable
    private fun DescriptionText() {
        Text(
            text = "Cliquez sur un avaloir trouvé ou ajouter un autre",
            modifier = Modifier.padding(5.dp)
        )
    }

    @Composable
    private fun LoadScreen() {
        Text(text = "Recherche en cours...")
        CircularProgressIndicator(progress = 0.5f)
    }

    private fun onClickAddButton(
        avaloirViewModel: AvaloirViewModel,
        context: Context
    ) {
        locationViewModel.stopLocation()
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
