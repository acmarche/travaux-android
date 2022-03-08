package be.marche.apptravaux.screens.avaloir

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.AvaloirUiState
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.AvaloirWidget
import be.marche.apptravaux.screens.widgets.ErrorDialog
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel

class AvaloirListScreen(val navController: NavController) {

    @Composable
    fun ListScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Liste des avaloirs",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Left
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
                            }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    },
                    backgroundColor = Colors.Pink500,
                    elevation = AppBarDefaults.TopAppBarElevation
                )
            }
        ) {
            when (val state = avaloirViewModel.uiState.collectAsState().value) {
                is AvaloirUiState.Loading -> {
                    Log.d("ZEZE", "loading")
                }
                is AvaloirUiState.Error -> {
                    Log.d("ZEZE", "error")
                    ErrorDialog(state.message)
                }
                is AvaloirUiState.Loaded -> {
                    val widget = AvaloirWidget()
                    widget.LoadAvaloirs(state.data, navController)
                }
                is AvaloirUiState.Empty -> {
                    Log.d("ZEZE", "vide")
                    Column {
                        ErrorDialog("La liste est vide")
                        Divider(
                            modifier = Modifier.height(MEDIUM_PADDING),
                            color = MaterialTheme.colors.background
                        )

                        Button(
                            onClick = { navController.navigate(TravauxRoutes.AvaloirSyncScreen.route) }
                        ) {
                            Text(text = "Synchroniser les données")
                        }
                        Divider(
                            modifier = Modifier.height(MEDIUM_PADDING),
                            color = MaterialTheme.colors.background
                        )
                    }
                }
            }
        }
    }


}