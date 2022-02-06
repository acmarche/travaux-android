package be.marche.apptravaux.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import be.marche.apptravaux.navigation.TravauxScreens
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

data class CardData(val texte: String, val url: String)

@Composable
fun HomeScreen(navController: NavController) {

    val navHostController = rememberNavController()

    val c = CardData("Gestion des avaloirs", TravauxScreens.AvaloirHomeScreen.route)
    val d = CardData("Gestion des stocks", TravauxScreens.StockHomeScreen.route)
    val e = CardData("Demo", TravauxScreens.DemoScreen.route)
    val cards: List<CardData> = listOf(c, d, e)

    MainContentHome(navController = navController, datas = cards)
}

@Composable
fun MainContentHome(
    navController: NavController,
    datas: List<CardData>
) {
    val context = LocalContext.current
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