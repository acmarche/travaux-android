package be.marche.apptravaux.screens

import android.icu.text.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.MapNew
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class AvaloirDetailScreen(
    val navController: NavController,
    val avaloirViewModel: AvaloirViewModel,
) {
    @Composable
    fun AvaloirDetailScreenMain(
        avaloirId: Int?
    ) {
        Timber.d("${avaloirId}")

        LaunchedEffect(avaloirId) { // only launch once whenever cardId change
            Timber.d(" effet ${avaloirId}")
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
    private fun AvaloirDetailContentScreen(
        avaloir: Avaloir
    ) {
        val singapore = LatLng(avaloir.latitude, avaloir.longitude)
        var isMapLoaded by remember { mutableStateOf(false) }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 18f)
        }
        avaloirViewModel.getDatesAvaloir(avaloir.idReferent)
        avaloirViewModel.getCommentaireAvaloir(avaloir.idReferent)

        val map = MapNew()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
        )
        {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 25.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    avaloir.imageUrl?.let {
                        Image(
                            painter = rememberImagePainter(avaloir.imageUrl),
                            contentDescription = null,
                            modifier = Modifier.size(128.dp)
                        )
                    }
                    Column() {
                        Text(text = "${avaloir.rue}", fontWeight = FontWeight.Bold)
                        Text(text = "${avaloir.localite} ", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Localisation: ${avaloir.latitude} ${avaloir.longitude}",
                            style = TextStyle(
                                color = Color.LightGray,
                                fontSize = 14.sp
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

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top,
                ) {
                    Button(
                        onClick = { updateClean(avaloir) },
                    ) {
                        Text(text = "C'est nettoyé")
                    }
                    Button(
                        onClick = {
                            navController.navigate(TravauxRoutes.AvaloirCommentaireScreen.route + "/${avaloir.idReferent}")
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
                Box(Modifier.height(350.dp)) {
                    map.GoogleMapView(
                        modifier = Modifier,
                        cameraPositionState = cameraPositionState,
                        position = singapore,
                        onMapLoaded = {
                            isMapLoaded = true
                        },
                    )

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
        Timber.d("dates $dates")
        val builder = StringBuilder()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

        dates.forEach { date ->
            builder.append(format.format(date.createdAt))
            builder.append(System.getProperty("line.separator"));
        }
        Text(
            text = "Dates de nettoyages",
            style = TextStyle(
                textDecoration = TextDecoration.Underline,
                color = Color.Green,
                fontSize = 18.sp
            )
        )
        Text(text = builder.toString())
        Spacer(modifier = Modifier.height(10.dp))
    }

    @Composable
    private fun CommentairesContent() {
        val commentaires = avaloirViewModel.commentairesAvaloir.collectAsState().value
        Text(
            text = "Commentaires",
            style = TextStyle(
                textDecoration = TextDecoration.Underline,
                color = Color.Green,
                fontSize = 18.sp
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
        val timeStamp = Date()
        val dateNettoyage = DateNettoyage(null, 0, avaloir.idReferent, timeStamp)
        avaloirViewModel.insertDateNettoyageDb(dateNettoyage)
    }

    fun formatDate(createdAt: Date): String {
        return DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_DAY).format(createdAt)
    }

}