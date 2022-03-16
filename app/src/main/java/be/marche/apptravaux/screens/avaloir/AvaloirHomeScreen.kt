package be.marche.apptravaux.screens.avaloir

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.CardData
import be.marche.apptravaux.screens.widgets.CardRow
import be.marche.apptravaux.ui.theme.Colors

class AvaloirHomeScreen(val navController: NavController) {

    @Composable
    fun HomeScreen() {
        val a =
            CardData(
                "Localiser et ajouter",
                { navController.navigate(TravauxRoutes.AvaloirSearchScreen.route) })
        val b = CardData(
            "Rechercher",
            { navController.navigate(TravauxRoutes.AvaloirListScreen.route) }
        )
        val c =
            CardData(
                "Synchronisation",
                { navController.navigate(TravauxRoutes.AvaloirSyncScreen.route) })

        val d =
            CardData(
                "Mes brouillons",
                { navController.navigate(TravauxRoutes.AvaloirDraftsScreen.route) })

        val cards: List<CardData> = listOf(a, b, c, d)

        MainContentAvaloirHome(cards)
    }

    @Composable
    fun MainContentAvaloirHome(
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
            Column(modifier = Modifier.padding(12.dp)) {
                LazyColumn {
                    items(datas) { data ->
                        CardRow(data.texte, data.action)
                    }
                }
            }
        }

    }
}