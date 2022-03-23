package be.marche.apptravaux.screens.avaloir

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.AvaloirUiState
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.AvaloirWidget
import be.marche.apptravaux.screens.widgets.ErrorDialog
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel

class AvaloirListScreen(val navController: NavController) {

    @Composable
    fun ListScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        LaunchedEffect(true) {
            avaloirViewModel.fetchAvaloirsFromDb()
        }

        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Liste des avaloirs"
                ) { navController.navigate(TravauxRoutes.AvaloirHomeScreen.route) }
            }
        ) {
            when (val state = avaloirViewModel.uiState.collectAsState().value) {
                is AvaloirUiState.Loading -> {
                }
                is AvaloirUiState.Error -> {
                    ErrorDialog(state.message)
                }
                is AvaloirUiState.Loaded -> {
                    val widget = AvaloirWidget()
                    widget.LoadAvaloirs(state.data, navController)
                }
                is AvaloirUiState.Empty -> {
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