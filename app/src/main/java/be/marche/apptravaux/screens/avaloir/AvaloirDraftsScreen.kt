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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.AvaloirDraft
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberImagePainter
import com.myricseptember.countryfactcomposefinal.widgets.CardRow

class AvaloirDraftsScreen(
    val navController: NavController,
) {

    @Composable
    fun ListScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Liste des avaloirs sur le mobile",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
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
            avaloirViewModel.refreshDrafts()
            val avaloirs =
                avaloirViewModel.allAvaloirsDraftsFlow.collectAsState()
            Column {
                Text(
                    text = "${avaloirs.value.count()} avaloirs brouillons",
                    style = MaterialTheme.typography.subtitle2
                )
                Divider(
                    modifier = Modifier.height(MEDIUM_PADDING),
                    color = MaterialTheme.colors.background
                )
                LoadAvaloirs(avaloirs.value, navController)
            }
        }
    }

    @Composable
    fun LoadAvaloirs(
        avaloirs: List<AvaloirDraft>,
        navController: NavController
    ) {
        LazyColumn {
            items(items = avaloirs) { avaloir ->
                CardRow(avaloir.latitude.toString(), {})
            }
        }
    }


}