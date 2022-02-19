package be.marche.apptravaux.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.GoogleMapWidget
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberImagePainter

class AvaloirDetailScreen(
    val navController: NavController,
    val avaloirViewModel: AvaloirViewModel,
) {
    @Composable
    fun AvaloirDetailScreenMain(
        avaloirId: Int?
    ) {
        Log.d("ZEZE avaloir id", "${avaloirId}")

        LaunchedEffect(avaloirId) { // only launch once whenever cardId change
            Log.d("ZEZE effect avaloir id", "${avaloirId}")
            if (avaloirId != null) {
                avaloirViewModel.getSelectedAvaloir(avaloirId)
            }
        }

        val selectedAvaloir by avaloirViewModel.selectedAvaloir.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Détail avaloir",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.AvaloirListScreen.route)
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
            selectedAvaloir.let {
                if (it != null) {
                    AvaloirDetailContentScreen(it)
                } else {
                    Text(text = "Avaloir non trouvé")
                }
            }
        }
    }

    @Composable
    fun AvaloirDetailContentScreen(
        avaloir: Avaloir
    ) {
        Log.d("ZEZE avaloir detail", "${avaloir}")

        Column {
            avaloir.imageUrl?.let {
                Image(
                    painter = rememberImagePainter(avaloir.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )
            }
            Text(text = "rue ${avaloir.rue} ", fontWeight = FontWeight.Bold)
            Text(text = "id ${avaloir.idReferent} ")
            GoogleMapWidget(
                avaloir.latitude,
                avaloir.longitude,
                avaloir.rue,
                false
            )
        }
    }
}