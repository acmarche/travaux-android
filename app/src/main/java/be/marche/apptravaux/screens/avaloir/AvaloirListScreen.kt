package be.marche.apptravaux.screens.avaloir

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.entities.AvaloirUiState
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberImagePainter
import com.myricseptember.countryfactcomposefinal.widgets.ErrorDialog

@Composable
fun AvaloirListScreen(
    navController: NavController,
    avaloirViewModel: AvaloirViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Liste des avaloirs",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) {
        when (val state = avaloirViewModel.uiState.collectAsState().value) {
            is AvaloirUiState.Loading -> {
                Log.d("ZEZE", "loading")
            }
            is AvaloirUiState.Error -> {
                Log.d("ZEZE", "error")
                ErrorDialog(state.message)
            }
            is AvaloirUiState.Loaded -> {
                Log.d("ZEZE", "loaded")
                LoadAvaloirs(state.data, navController)
            }
            else -> {

            }
        }
    }
}

@Composable
fun LoadAvaloirs(
    avaloirs: List<Avaloir>,
    navController: NavController
) {
    LazyColumn {
        items(items = avaloirs) { avaloir ->
            ItemAvaloir(avaloir) { avoirId ->
                navController.navigate(TravauxRoutes.AvaloirDetailScreen.route + "/${avaloir.idReferent}")
            }
        }
    }
}

@Composable
fun ItemAvaloir(
    avaloir: Avaloir,
    onItemCLick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .clickable {
                Log.e("ZEZE", "id ${avaloir.idReferent}")
                onItemCLick(avaloir.idReferent)
            },
    ) {
        Image(
            painter = rememberImagePainter(avaloir.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(128.dp)
                .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        val surfaceColor: Color by animateColorAsState(
            MaterialTheme.colors.primary
        )

        Column() {
            Text(
                text = "Rue: ${avaloir.rue}",
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                // surfaceColor color will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = "Localité: ${avaloir.localite}",
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}
