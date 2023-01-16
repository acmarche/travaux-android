package be.marche.apptravaux.screens.avaloir

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.CardData
import be.marche.apptravaux.screens.widgets.CardRow
import be.marche.apptravaux.screens.widgets.TopAppBarJf

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
                "Mes brouillons",
                { navController.navigate(TravauxRoutes.AvaloirDraftsScreen.route) })

        val cards: List<CardData> = listOf(a, b, c)

        MainContentAvaloirHome(cards)
    }

    @Composable
    fun MainContentAvaloirHome(
        datas: List<CardData>
    ) {
        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Gestion des avaloirs"
                ) { navController.navigate(TravauxRoutes.HomeScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

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
}