package be.marche.apptravaux.screens.avaloir

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.networking.ConnectionState
import be.marche.apptravaux.networking.connectivityState
import be.marche.apptravaux.screens.widgets.AvaloirWidget
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.ui.theme.ScreenSizeTheme
import be.marche.apptravaux.utils.DateUtils.Companion.formatDateTime
import be.marche.apptravaux.utils.DownloadHelper
import be.marche.apptravaux.viewModel.AvaloirViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class AvaloirDraftsScreen(
    val navController: NavController,
) {
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ListScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Données en brouillons"
                ) { navController.navigate(TravauxRoutes.AvaloirHomeScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

                avaloirViewModel.refreshDrafts()
                val avaloirs =
                    avaloirViewModel.allAvaloirsDraftsFlow.collectAsState()
                val dates =
                    avaloirViewModel.allDatesDraftsFlow.collectAsState()
                val commentaires =
                    avaloirViewModel.allCommentairesDraftsFlow.collectAsState()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${avaloirs.value.count()} avaloirs brouillons",
                        style = MaterialTheme.typography.h5
                    )
                    Text(
                        text = "${dates.value.count()} dates de nettoyages brouillons",
                        style = MaterialTheme.typography.h5
                    )
                    Text(
                        text = "${commentaires.value.count()} commentaires brouillons",
                        style = MaterialTheme.typography.h5
                    )
                    Divider(
                        modifier = Modifier.height(MEDIUM_PADDING),
                        color = MaterialTheme.colors.background
                    )
                    Button(
                        onClick = { navController.navigate(TravauxRoutes.SyncScreen.route) }
                    ) {
                        Text(text = "Synchroniser les données")
                    }
                    Divider(
                        modifier = Modifier.height(MEDIUM_PADDING),
                        color = MaterialTheme.colors.background
                    )
                    val context = LocalContext.current
                    FruitListAnimation(avaloirs.value, context, avaloirViewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ExperimentalAnimationApi
    @Composable
    fun FruitListAnimation(
        avaloirList: List<Avaloir>,
        context: Context,
        avaloirViewModel: AvaloirViewModel
    ) {
        val widget = AvaloirWidget()
        val deletedFruitList = remember { mutableStateListOf<Avaloir>() }

        val connection by connectivityState()
        val isConnected = connection == ConnectionState.Available

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(
                        items = avaloirList,
                        itemContent = { _, avaloir ->
                            AnimatedVisibility(
                                visible = !deletedFruitList.contains(avaloir),
                                enter = expandVertically(),
                                exit = shrinkVertically(
                                    animationSpec = tween(
                                        durationMillis = 1000
                                    )
                                )
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp, 5.dp, 10.dp, 5.dp)
                                        .background(Color.White),
                                    elevation = 10.dp,
                                    shape = RoundedCornerShape(5.dp)
                                ) {
                                    Column(
                                        //  modifier = Modifier.padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            val downloadHelper = DownloadHelper(context)
                                            val imgPath =
                                                widget.ImageAvaloirPath(
                                                    avaloir,
                                                    isConnected,
                                                    downloadHelper
                                                )

                                            widget.ImageAvaloir(
                                                imgPath,
                                                ScreenSizeTheme.dimens.width70,
                                                ScreenSizeTheme.dimens.height70,
                                                ContentScale.FillHeight
                                            )
                                            Column {
                                                Text(
                                                    text = "Ajouté le ${formatDateTime(avaloir.createdAt)}",
                                                    style = TextStyle(
                                                        color = Color.Black,
                                                        fontSize = ScreenSizeTheme.textStyle.fontWidth_1,
                                                        textAlign = TextAlign.Left
                                                    ),
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                                Text(
                                                    text = "Coordonnées ${avaloir.latitude}, ${avaloir.longitude}",
                                                    style = TextStyle(
                                                        color = Color.Black,
                                                        fontSize = ScreenSizeTheme.textStyle.fontWidth_1,
                                                        textAlign = TextAlign.Left
                                                    ),
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    avaloirViewModel.deleteAvaloirDraft(avaloir)
                                                    deletedFruitList.add(avaloir)
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "Deletion"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}