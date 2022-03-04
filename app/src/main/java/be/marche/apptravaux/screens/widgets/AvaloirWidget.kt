package be.marche.apptravaux.screens.widgets

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
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import coil.compose.rememberImagePainter
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

class AvaloirWidget {

    @Composable
    fun LoadAvaloirs(
        avaloirs: List<Avaloir>,
        navController: NavController
    ) {
        Log.d("ZEZE", "loaded count ${avaloirs.count()}")
        LazyColumn {
            items(avaloirs) { avaloir ->
                CardRow(avaloir.rue!!, {})
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
                painter = rememberImagePainter(avaloir.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
                    .clip(CircleShape)
            )

            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )

            val surfaceColor: Color by animateColorAsState(
                MaterialTheme.colors.primary
            )

            Column() {
                Text(
                    text = "Rue: ${avaloir.rue}",
                    color = MaterialTheme.colors.secondaryVariant,
                    style = MaterialTheme.typography.subtitle2
                )

                Divider(
                    modifier = Modifier.height(MEDIUM_PADDING),
                    color = MaterialTheme.colors.background
                )

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
}