package be.marche.apptravaux.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxScreens
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

data class CardData(val texte: String, val url: String)

@Composable
fun HomeScreen(navController: NavController) {
    val c = CardData("Gestion des avaloirs", TravauxScreens.AvaloirListScreen.route)
    val d = CardData("Gestion des stocks", TravauxScreens.MainScreen.route)
    val cards: List<CardData> = listOf(c, d)

    MainContent(navController = navController, datas = cards)
}

@Composable
fun MainContent(
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