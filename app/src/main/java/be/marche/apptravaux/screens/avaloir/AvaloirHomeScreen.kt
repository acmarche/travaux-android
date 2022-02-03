package be.marche.apptravaux.screens.avaloir

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxScreens
import be.marche.apptravaux.screens.CardData
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

@Composable
fun AvaloirHomeScreen(navController: NavController) {
    val c = CardData("Rechercher", TravauxScreens.AvaloirSearchScreen.route)
    val d = CardData("Liste des avaloirs", TravauxScreens.AvaloirListScreen.route)
    val cards: List<CardData> = listOf(c, d)

    MainContentAvaloirHome(navController = navController, datas = cards)
}

@Composable
fun MainContentAvaloirHome(
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