package be.marche.apptravaux.screens.avaloir

import android.content.Context
import android.icu.text.DateFormat
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

    @Composable
    fun ListScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Avaloirs sur le mobile",
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

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${avaloirs.value.count()} avaloirs brouillons",
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
                LoadAvaloirs(avaloirs.value, navController)
            }
        }
    }

    @Composable
    fun LoadAvaloirs(
        avaloirs: List<AvaloirDraft>,
        navController: NavController
    ) {
        val context = LocalContext.current
        val message = stringResource(R.string.toast_draft)

        LazyColumn {
            items(avaloirs) { avaloir ->
                ItemAvaloirDraft(avaloir, context) {
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @Composable
    fun ItemAvaloirDraft(
        avaloir: AvaloirDraft,
        context: Context,
        onItemCLick: (Int) -> Unit
    ) {
        Card(
            modifier = Modifier
                .clickable {
                    avaloir.id?.let { onItemCLick(it) }
                }
                .padding(10.dp)
                .fillMaxSize(),
            elevation = 5.dp,
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ImageCache(avaloir, context)
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Location: ${avaloir.latitude} ${avaloir.longitude}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    val localDate = formatDate(avaloir.createdAt)
                    Text(
                        text = "Ajouté le: ${localDate}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
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
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .padding(5.dp)
            )
        } else {
            Image(
                painterResource(R.drawable.profile_picture),
                contentDescription = "Image",
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

}