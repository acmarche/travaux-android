package be.marche.apptravaux.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Avaloir
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
        val scrollableState = rememberScrollState()

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
        val locationState = remember {
            mutableStateOf(avaloirViewModel.currentLatLng)
        }
        val singapore = LatLng(avaloir.latitude, avaloir.longitude)
        var isMapLoaded by remember { mutableStateOf(false) }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 18f)
        }
        avaloirViewModel.getDatesAvaloir(avaloir.idReferent)
        val map = MapNew()

        LazyColumn()
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
                DatesContent(avaloir)
            }

            item {
                DatesContent(avaloir)
            }

            item {
                Box(Modifier.fillMaxSize()) {
                    map.GoogleMapView(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = {
                            isMapLoaded = true
                        },
                    )

                    if (!isMapLoaded) {
                        AnimatedVisibility(
                            modifier = Modifier
                                .matchParentSize(),
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
        }
    }

    @Composable
    private fun DatesContent(
        avaloir: Avaloir
    ) {
        val dates = avaloirViewModel.datesAvaloir.collectAsState().value
        Timber.d("dates $dates")
        val builder = StringBuilder()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

        dates.forEach { date ->
            builder.append(format.format(date.date))
            builder.append(System.getProperty("line.separator"));
        }
        Text(
            text = "Dates de nettoyages",
            style = TextStyle(
                color = Color.LightGray,
                fontSize = 18.sp
            )
        )
        Text(text = builder.toString())
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
        Button(
            onClick = { updateClean(avaloir) },
        ) {
            Text(text = "C'est nettoyé")
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    private fun updateClean(avaloir: Avaloir) {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        avaloirViewModel.addCleaningDateAsync(avaloir, timeStamp)
    }

}