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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.navigation.TravauxScreens
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.myricseptember.countryfactcomposefinal.widgets.ErrorDialog

@Composable
fun AvaloirListScreen(
    navController: NavController,
    avaloirViewModel: AvaloirViewModel
) {
    when (val state = avaloirViewModel.uiState.collectAsState().value) {
        is AvaloirViewModel.AvaloirUiState.Loading -> {
            Log.d("ZEZE", "loading")
        }
        is AvaloirViewModel.AvaloirUiState.Error -> {
            Log.d("ZEZE", "error")
            ErrorDialog(state.message)
        }
        is AvaloirViewModel.AvaloirUiState.Loaded -> {
            Log.d("ZEZE", "loaded")
            LoadAvaloirs(state.data, navController)
        }
    }
}

@Composable
fun LoadAvaloirs(
    avaloirs: List<Avaloir>,
    navController: NavController,
) {
    LazyColumn {
        items(items = avaloirs) { avaloir ->
            ItemAvaloir(avaloir) { avoirId ->
                Log.d("ZEZE", "id {$avoirId}")
                navController.navigate(TravauxScreens.AvaloirDetailScreen.route + "/$avoirId")
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
                onItemCLick(avaloir.idReferent)
            },
    ) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // We keep track if the message is expanded or not in this
        // variable
        var isExpanded by remember { mutableStateOf(false) }
        // surfaceColor will be updated gradually from one color to the other
        val surfaceColor: Color by animateColorAsState(
            if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        )

        // We toggle the isExpanded variable when we click on this Column
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = "rue: ${avaloir.rue}",
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
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}
