package be.marche.apptravaux.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.networking.ConnectionState
import be.marche.apptravaux.networking.connectivityState
import be.marche.apptravaux.screens.widgets.AvaloirWidget
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.ui.theme.ScreenSizeTheme
import be.marche.apptravaux.utils.DateUtils
import be.marche.apptravaux.utils.DateUtils.Companion.formatDate
import be.marche.apptravaux.utils.DateUtils.Companion.formatDateTime
import be.marche.apptravaux.utils.DownloadHelper
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class AvaloirDetailScreen(
    val navController: NavController,
    val avaloirViewModel: AvaloirViewModel,
) {
    @Composable
    fun AvaloirDetailScreenMain(
        avaloirId: Int?
    ) {
        LaunchedEffect(avaloirId) { // only launch once whenever cardId change
            if (avaloirId != null) {
                avaloirViewModel.getSelectedAvaloir(avaloirId)
            }
        }

        val selectedAvaloir by avaloirViewModel.selectedAvaloir.collectAsState()

        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Détail avaloir"
                ) { navController.navigate(TravauxRoutes.AvaloirListScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

                selectedAvaloir.let {
                    if (it != null) {
                        AvaloirDetailContentScreen(it)
                    } else {
                        Text(text = "Avaloir non trouvé")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    private fun AvaloirDetailContentScreen(
        avaloir: Avaloir
    ) {
        val singapore = LatLng(avaloir.latitude, avaloir.longitude)
        var isMapLoaded by remember { mutableStateOf(false) }

        avaloirViewModel.getDatesAvaloir(avaloir.idReferent)
        avaloirViewModel.getCommentaireAvaloir(avaloir.idReferent)

        val connection by connectivityState()
        val isConnected = connection == ConnectionState.Available

        val context = LocalContext.current
        val messageDate = stringResource(R.string.dateCleanNoPublished)
        val messageCommentaire = stringResource(R.string.commentaireNoPublished)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 25.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val downloadHelper = DownloadHelper(context)
                    val widget = AvaloirWidget()
                    val imgPath = widget.ImageAvaloirPath(avaloir, isConnected, downloadHelper)

                    widget.ImageAvaloir(
                        imgPath,
                        ScreenSizeTheme.dimens.imageW,
                        ScreenSizeTheme.dimens.imageH,
                        ContentScale.Crop,
                        25.dp
                    )
                    Column {
                        val texteRue = avaloir.rue ?: "Non déterminé"
                        Text(
                            text = texteRue,
                            fontWeight = FontWeight.Bold,
                            fontSize = ScreenSizeTheme.textStyle.fontWidth_1
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        val texteLocalite = avaloir.localite ?: "Non déterminé"
                        Text(
                            text = texteLocalite,
                            fontWeight = FontWeight.Bold,
                            fontSize = ScreenSizeTheme.textStyle.fontWidth_1
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "Ajouté le ${formatDateTime(avaloir.createdAt)}",
                            style = ScreenSizeTheme.textStyle.fontStyle_1,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "Localisation: ${avaloir.latitude} ${avaloir.longitude}",
                            style = TextStyle(
                                color = Color.LightGray,
                                fontSize = ScreenSizeTheme.textStyle.fontWidth_1
                            )
                        )
                        Text(text = "Id: ${avaloir.idReferent} ")
                    }
                }
            }

            item {
                Divider(
                    modifier = Modifier.height(MEDIUM_PADDING),
                    color = MaterialTheme.colors.background
                )
            }

            val isDraft: Boolean = avaloir.idReferent == 0
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top,
                ) {
                    Button(
                        onClick = {
                            if (isDraft) {
                                Toast.makeText(
                                    context,
                                    messageDate,
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                updateClean(avaloir)
                            }
                        },
                    ) {
                        Text(text = "C'est nettoyé")
                    }
                    Button(
                        onClick = {
                            if (isDraft) {
                                Toast.makeText(
                                    context,
                                    messageCommentaire,
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                navController.navigate(TravauxRoutes.AvaloirCommentaireScreen.route + "/${avaloir.idReferent}")
                            }
                        },
                    ) {
                        Text(text = "Ajouter un commentaire")
                    }
                }
            }
            item {
                Divider(
                    modifier = Modifier.height(MEDIUM_PADDING),
                    color = MaterialTheme.colors.background
                )
            }
            item {
                DatesContent()
            }

            item {
                CommentairesContent()
            }
            item {
                Box(Modifier.height(ScreenSizeTheme.dimens.carte)) {

                    val mapProperties by remember {
                        mutableStateOf(
                            MapProperties(maxZoomPreference = 21f, minZoomPreference = 3f)
                        )
                    }
                    val mapUiSettings by remember {
                        mutableStateOf(
                            MapUiSettings(mapToolbarEnabled = true)
                        )
                    }
                    val state: MarkerState = rememberMarkerState(position = singapore)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(singapore, 18f)
                    }
                    GoogleMap(
                        properties = mapProperties,
                        uiSettings = mapUiSettings,
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(state)
                        //Marker(position = singapore)
                    }
                    isMapLoaded = true
                    if (!isMapLoaded) {
                        AnimatedVisibility(
                            modifier = Modifier.matchParentSize(),
                            visible = !isMapLoaded,
                            enter = EnterTransition.None,
                            exit = fadeOut()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .background(MaterialTheme.colors.background)
                                    .wrapContentSize()
                            )
                        }
                    }
                }
            }
            item {
                Divider(
                    modifier = Modifier.height(MEDIUM_PADDING),
                    color = MaterialTheme.colors.background
                )
            }
        }
    }

    @Composable
    private fun DatesContent() {
        val dates = avaloirViewModel.datesAvaloir.collectAsState().value
        val builder = StringBuilder()

        dates.forEach { date ->
            builder.append(formatDate(date.createdAt))
            builder.append(System.getProperty("line.separator"));
        }
        Text(
            text = "Dates de nettoyages",
            style = TextStyle(
                textDecoration = TextDecoration.Underline,
                color = Color.Green,
                fontSize = ScreenSizeTheme.textStyle.fontTitle_1
            )
        )
        Text(text = builder.toString())
        Spacer(modifier = Modifier.height(10.dp))
    }

    @Composable
    private fun CommentairesContent() {
        val commentaires = avaloirViewModel.commentairesAvaloir.collectAsState().value
        if (commentaires.count() > 0) {
            Text(
                text = "Commentaires",
                style = TextStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Color.Green,
                    fontSize = ScreenSizeTheme.textStyle.fontTitle_1
                )
            )

            Column {
                commentaires.forEach() { commentaire ->
                    ItemCommentaire(commentaire) {

                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    @Composable
    fun ItemCommentaire(
        commentaire: Commentaire,
        onItemCLick: (Int) -> Unit
    ) {
        Card(
            modifier = Modifier
                .clickable {
                    commentaire.id?.let { onItemCLick(it) }
                }
                .padding(10.dp)
                .fillMaxSize(),
            elevation = 5.dp,
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = commentaire.content,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    Text(
                        text = "Ajouté le: ${formatDate(commentaire.createdAt)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }

    private fun updateClean(avaloir: Avaloir) {
        val dateNettoyage = DateNettoyage(null, 0, avaloir.idReferent, DateUtils.dateToday())
        avaloirViewModel.insertDateNettoyageDb(dateNettoyage)
    }

}