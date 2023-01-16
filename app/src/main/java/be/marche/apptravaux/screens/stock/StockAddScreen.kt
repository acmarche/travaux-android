package be.marche.apptravaux.screens.stock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.viewModel.StockViewModel

class StockAddScreen(val navController: NavController) {

    @Composable
    fun Main(stockViewModel: StockViewModel = viewModel()) {
        MainContent()
    }

    @Composable
    private fun MainContent(
    ) {
        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Gestion des stocks"
                ) { navController.navigate(TravauxRoutes.StockHomeScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

                Column(modifier = Modifier.padding(12.dp)) {
                    LazyColumn {

                    }
                }
            }
        }
    }
}