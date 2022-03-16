package be.marche.apptravaux.screens.stock

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.CardData
import be.marche.apptravaux.screens.widgets.CardRow
import be.marche.apptravaux.ui.theme.Colors

class StockHomeScreen(val navController: NavController) {

    @Composable
    fun HomeScreen() {
        val context = LocalContext.current
        val a =
            CardData(
                "Rechercher",
                { navController.navigate(TravauxRoutes.StockListScreen.route) })
        val b = CardData(
            "Ajouter un produit",
            {
                Toast.makeText(
                    context,
                    "Bientôt...",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        val c =
            CardData(
                "Brouillons",
                { navController.navigate(TravauxRoutes.StockDraftScreen.route) })

        val cards: List<CardData> = listOf(a, b, c)
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
                            text = "Gestion des stocks",
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