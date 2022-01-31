package be.marche.apptravaux.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import be.marche.apptravaux.viewModel.AvaloirViewModel

@Composable
fun AvaloirDetailScreen(
    navController: NavController,
    avaloirViewModel: AvaloirViewModel,
    avaloirId: Int?
) {
    if (avaloirId != null) {
        if (avaloirId > 0) {
            val avaloir = avaloirViewModel.findById(avaloirId).collectAsState(null).value
            Column {
                if (avaloir != null) {
                    Text(text = "rue ${avaloir.rue} ", fontWeight = FontWeight.Bold)
                    Text(text = "id ${avaloir.idReferent} ")
                } else {
                    Text(text = "Not found")
                }
            }
        }
    } else {
        Text(text = "Bug")
    }

}