package be.marche.apptravaux.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import be.marche.apptravaux.navigation.TravauxRoutes
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

data class CardData(val texte: String, val url: String)

@Composable
fun HomeScreen(navController: NavController) {

    val c = CardData("Gestion des avaloirs", TravauxRoutes.AvaloirHomeScreen.route)
    val d = CardData("Gestion des stocks", TravauxRoutes.StockHomeScreen.route)
    val e = CardData("Demo", TravauxRoutes.DemoScreen.route)
    val cards: List<CardData> = listOf(c, d, e)

    MainContentHome(navController = navController, datas = cards)
}

@Composable
fun MainContentHome(
    navController: NavController,
    datas: List<CardData>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Appli travaux",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            LazyColumn {
                items(datas) { data ->
                    CardRow(data) {
                        navController.navigate(data.url)
                    }
                }
            }
        }

    }
}