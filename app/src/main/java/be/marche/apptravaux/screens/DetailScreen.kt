package be.marche.apptravaux.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@Composable
fun DetailScreen(navController: NavController, countryId: Int?) {
    Row {
        Text(text = "Monetary Unit", fontWeight = FontWeight.Bold)
        Text(text = "id ${countryId} ")
    }
}