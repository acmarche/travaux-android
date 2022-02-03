package be.marche.apptravaux.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.screens.widgets.GoogleMapWidget
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberImagePainter

@Composable
fun AvaloirDetailScreen(
    navController: NavController,
    avaloirViewModel: AvaloirViewModel,
    avaloirId: Int?
) {
    var item by remember { mutableStateOf<Avaloir?>(null) }
    val (text, setText) = remember { mutableStateOf("") }

    LaunchedEffect(avaloirId) { // only launch once whenever cardId change
        if (avaloirId != null) {
            item = null //getX
        }
    }

    if (avaloirId != null) {
        if (avaloirId > 0) {
            val avaloir = avaloirViewModel.findById(avaloirId).collectAsState(initial = null).value
            Log.d("ZEZE", "${avaloir}")

            Column {
                avaloir?.imageUrl?.let {
                    Image(
                        painter = rememberImagePainter(avaloir.imageUrl),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp)
                    )
                }
                if (avaloir != null) {
                    Text(text = "rue ${avaloir.rue} ", fontWeight = FontWeight.Bold)
                    Text(text = "id ${avaloir.idReferent} ")
                    GoogleMapWidget(avaloir.latitude, avaloir.longitude, avaloir.rue)
                } else {
                    Text(text = "Not found")
                }
            }
        }
    } else {
        Text(text = "Bug")
    }

}