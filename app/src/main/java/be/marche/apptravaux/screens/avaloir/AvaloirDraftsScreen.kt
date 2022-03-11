package be.marche.apptravaux.screens.avaloir

import android.content.Context
import android.icu.text.DateFormat
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.AvaloirDraft
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.utils.FileHelper
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberImagePainter
import java.io.File
import java.util.*

class AvaloirDraftsScreen(
    val navController: NavController,
) {

    val fileHelper = FileHelper()

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ListScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Données en brouillons",
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
                    onClick = { navController.navigate(TravauxRoutes.AvaloirSyncScreen.route) }
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

    @Composable
    fun ImageCache(
        avaloir: AvaloirDraft,
        context: Context
    ) {
        var fileUri: Uri? = null
        try {
            val cacheFile = File(avaloir.imageUrl)
            fileUri = fileHelper.createUri(context, cacheFile)
        } catch (e: Exception) {

        }
        if (fileUri != null) {
            Image(
                rememberImagePainter(fileUri),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .padding(5.dp)
            )
        } else {
            Image(
                painterResource(R.drawable.profile_picture),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .padding(5.dp)
            )
        }
    }

    fun formatDate(createdAt: Date): String {
        return DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_DAY).format(createdAt)
    }

    @ExperimentalAnimationApi
    @Composable
    fun FruitListAnimation(
        avaloirList: List<AvaloirDraft>,
        context: Context,
        avaloirViewModel: AvaloirViewModel
    ) {
        val deletedFruitList = remember { mutableStateListOf<AvaloirDraft>() }
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
                                        .height(100.dp)
                                        .padding(10.dp, 5.dp, 10.dp, 5.dp)
                                        .background(Color.White),
                                    elevation = 10.dp,
                                    shape = RoundedCornerShape(5.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            ImageCache(avaloir, context)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Localisation: ${avaloir.latitude} ${avaloir.longitude}",
                                                    style = TextStyle(
                                                        color = Color.Black,
                                                        fontSize = 18.sp,
                                                        textAlign = TextAlign.Center
                                                    ),
                                                    modifier = Modifier.padding(16.dp)
                                                )
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
                        }
                    )
                }
            }
        }
    }
}