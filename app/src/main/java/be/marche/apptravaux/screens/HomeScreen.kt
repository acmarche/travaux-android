package be.marche.apptravaux.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxScreens

@Composable
fun HomeScreen(navController: NavController) {
    Text(text="Zeze")
    val countryId = 5
    navController.navigate(TravauxScreens.DetailScreen.route + "/$countryId")
}
