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
import be.marche.apptravaux.navigation.TravauxRoutes
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

data class CardData(val texte: String, val action: () -> Unit)

@Composable
fun HomeScreen(navController: NavController) {

    val c = CardData(
        "Gestion des avaloirs",
        { navController.navigate(TravauxRoutes.AvaloirHomeScreen.route) }
    )
    val d =
        CardData("Gestion des stocks",
            { navController.navigate(TravauxRoutes.StockHomeScreen.route) })

    val e = CardData("Demo", { navController.navigate(TravauxRoutes.AvaloirDraftsScreen.route) })

    val cards: List<CardData> = listOf(c, d, e)

    MainContentHome(datas = cards)
}

@Composable
fun MainContentHome(
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
                    CardRow(data.texte, data.action)
                }
            }
        }

    }
}