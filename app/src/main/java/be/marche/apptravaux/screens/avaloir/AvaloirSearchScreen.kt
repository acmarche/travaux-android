package be.marche.apptravaux.screens.avaloir

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.SearchResponseUiState
import be.marche.apptravaux.location.LocationService
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.google.android.libraries.maps.model.LatLng
import com.myricseptember.countryfactcomposefinal.widgets.ErrorDialog

class AvaloirSearchScreen(
    val navController: NavController,
    val avaloirViewModel: AvaloirViewModel
) {
    val service = LocationService()

    @Composable
    fun SearchMainScreen() {
        val content = remember { mutableStateOf("Home Screen") }
        val selectedItem = remember { mutableStateOf("home") }
        val openDialog = remember { mutableStateOf(false) }
        Log.d("ZEZE", "avaloir search screen")
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Recherche dans un rayon de 25m",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
                            }
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    backgroundColor = Colors.Pink500,
                    elevation = AppBarDefaults.TopAppBarElevation
                )
            },
            content = {
                BeginSearch(avaloirViewModel, navController)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(TravauxRoutes.AvaloirAddScreen.route)
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
                                selected = selectedItem.value == "Setting",
                                onClick = {
                                    content.value = "Setting Screen"
                                    selectedItem.value = "setting"
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
            }
        )
    }

    @Composable
    fun BeginSearch(
        avaloirViewModel: AvaloirViewModel = viewModel(),
        navController: NavController
    ) {
        Log.d("ZEZE", "searchScreen begin")
        service.getDeviceLocation(navController.context, avaloirViewModel)
        val location = avaloirViewModel.userCurrentLatLng.value
        ContentSearch(navController, latLng = location)
    }

    @Composable
    fun ContentSearch(
        navController: NavController,
        latLng: LatLng
    ) {
        Log.d("ZEZE", "searchScreen location {$latLng")

        Column(modifier = Modifier.padding(15.dp)) {
            LocationText(latLng)
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )
            DescriptionText()
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )
            if (latLng.latitude > 0.0) {
                LaunchedEffect(true) {
                    Log.d("ZEZE", "searchScreen searching {$latLng")
                    avaloirViewModel.search(latLng.latitude, latLng.longitude, "25m")
                }
                ResultSearch(avaloirViewModel, navController)
            }
        }
    }

    @Composable
    fun ResultSearch(
        avaloirViewModel: AvaloirViewModel = viewModel(),
        navController: NavController
    ) {
        Log.d("ZEZE", "searchScreen resultsearch")
        when (val state = avaloirViewModel.resultSearch.collectAsState().value) {
            is SearchResponseUiState.Loading -> {
                LoadScreen()
            }
            is SearchResponseUiState.Error -> {
                Log.d("ZEZE", "error")
                ErrorDialog(state.message)
            }
            is SearchResponseUiState.Loaded -> {
                Log.d("ZEZE", "loaded")
                Log.d("ZEZE", "search avaloirs ${state.response}")
                Text("${state.response.avaloirs.count()} avaloir(s) trouvé(s) dans un rayon de 25m")
                Divider(
                    modifier = Modifier.height(MEDIUM_PADDING),
                    color = MaterialTheme.colors.background
                )
                LoadAvaloirs(state.response.avaloirs, navController)
            }
            else -> {

            }
        }
    }

    @Composable
    fun LocationText(location: LatLng?) {
        if (location != null) {
            Text(text = "Votre localisation: ${location.latitude}, ${location.longitude}")
        } else {
            Text(text = "Pas de localisation")
        }
    }

    @Composable
    fun DescriptionText() {
        Text(
            text = "Cliquez sur un avaloir trouvé ou ajouter un autre",
            modifier = Modifier.padding(5.dp)
        )
    }

    @Composable
    fun LoadScreen() {
        Text(text = "Recherche en cours...")
        CircularProgressIndicator(progress = 0.5f)
        Log.d("ZEZE", "loading")
    }
}