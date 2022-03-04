package be.marche.apptravaux.screens.avaloir

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.AvaloirDraft
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.CardData
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

class AvaloirHomeScreen(val navController: NavController) {

    @Composable
    fun HomeScreen(avaloirViewModel: AvaloirViewModel = viewModel()) {
        val a =
            CardData(
                "Rechercher",
                { navController.navigate(TravauxRoutes.AvaloirSearchScreen.route) })
        val b = CardData(
            "Liste des avaloirs",
            { navController.navigate(TravauxRoutes.AvaloirListScreen.route) }
        )
        val c =
            CardData(
                "Synchronisation",
                { navController.navigate(TravauxRoutes.AvaloirSyncScreen.route) })

        val cards: List<CardData> = listOf(a, b, c)

        MainContentAvaloirHome(avaloirViewModel, navController, cards)
    }

    @Composable
    fun MainContentAvaloirHome(
        avaloirViewModel: AvaloirViewModel,
        navController: NavController,
        datas: List<CardData>
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Gestion des avaloirs",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.HomeScreen.route)
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

            val state = avaloirViewModel.allAvaloirsDraftFlow.collectAsState(initial = emptyList())
            Log.d("ZEZE", "drafts count " + state.value.count())

            Column(modifier = Modifier.padding(12.dp)) {
                LazyColumn {
                    items(state.value) { data ->
                        CardRow(data.latitude.toString(), {})
                    }
                }
            }
        }

    }
}