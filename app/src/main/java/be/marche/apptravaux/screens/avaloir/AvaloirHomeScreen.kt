package be.marche.apptravaux.screens.avaloir

import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import be.marche.apptravaux.AvaloirAddActivity
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.CardData
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

@Composable
fun AvaloirHomeScreen(navController: NavController) {
    val c = CardData("Rechercher", TravauxRoutes.AvaloirAddScreen.route)
    val d = CardData("Liste des avaloirs", TravauxRoutes.AvaloirListScreen.route)
    val cards: List<CardData> = listOf(c, d)

    MainContentAvaloirHome(navController = navController, datas = cards)
}

@Composable
fun MainContentAvaloirHome(
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
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            CardRow(CardData("Rechercher", TravauxRoutes.AvaloirAddScreen.route)) {
                val intent = Intent(navController.context, AvaloirAddActivity::class.java)
                ContextCompat.startActivity(navController.context, intent, null)
            }

            val data = CardData("Liste des avaloirs", TravauxRoutes.AvaloirListScreen.route)
            CardRow(CardData("Rechercher", TravauxRoutes.AvaloirAddScreen.route)) {
                navController.navigate(data.url)
            }

         /*   LazyColumn {
                items(datas) { data ->
                    CardRow(data) {
                        navController.navigate(data.url)
                    }
                }
            }*/
        }
    }

}