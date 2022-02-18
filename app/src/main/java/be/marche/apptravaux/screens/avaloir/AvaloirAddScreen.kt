package be.marche.apptravaux.screens.avaloir

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import be.marche.apptravaux.AvaloirAddActivity
import be.marche.apptravaux.entities.SearchResponseUiState
import be.marche.apptravaux.location.LocationService
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.google.android.libraries.maps.model.LatLng
import com.myricseptember.countryfactcomposefinal.widgets.ErrorDialog
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class AvaloirAddScreen(
    val avaloirViewModel: AvaloirViewModel
) {
    @ExperimentalMaterialApi
    @Composable
    fun TakePicure() {
        Log.d("ZEZE", "take picture")
        val context = LocalContext.current
        val bottomSheetModalState =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val coroutineScope = rememberCoroutineScope()

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { btm: Bitmap? ->
            AvaloirAddActivity.MyBitmap.bitmap = btm
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                cameraLauncher.launch()
                coroutineScope.launch {
                    bottomSheetModalState.hide()
                }
            } else {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }

        ModalBottomSheetLayout(
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colors.primary.copy(0.08f))
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Add Photo!",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            color = MaterialTheme.colors.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                        Divider(
                            modifier = Modifier
                                .height(1.dp)
                                .background(MaterialTheme.colors.primary)
                        )
                        Text(
                            text = "Take Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (PackageManager.PERMISSION_GRANTED) {
                                        ContextCompat.checkSelfPermission(
                                            context, Manifest.permission.CAMERA
                                        ) -> {
                                            cameraLauncher.launch()
                                            coroutineScope.launch {
                                                bottomSheetModalState.hide()
                                            }
                                        }
                                        else -> {
                                            permissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    }
                                }
                                .padding(15.dp),
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.SansSerif
                        )

                    }
                }
            },
            sheetState = bottomSheetModalState,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            modifier = Modifier
                .background(MaterialTheme.colors.background)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (!bottomSheetModalState.isVisible) {
                                bottomSheetModalState.show()
                            } else {
                                bottomSheetModalState.hide()
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Take Picture",
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }

        AvaloirAddActivity.MyBitmap.bitmap?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                contentDescription = "Image",
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .padding(top = 10.dp),
                contentScale = ContentScale.Fit
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SearchScreen(
        avaloirViewModel: AvaloirViewModel= viewModel(),
        navController: NavController
    ) {
        Log.d("ZEZE", "searchScreen")
        AppTravaux6Theme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Recherche dans la zone 25m",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                }
            ) {
                BeginSearch(avaloirViewModel, navController)
            }
        }
    }

    @Composable
    fun BeginSearch(
        avaloirViewModel: AvaloirViewModel= viewModel(),
        navController: NavController
    ) {
        Log.d("ZEZE", "searchScreen begin")
        val service = LocationService()
        service.getDeviceLocation(navController.context, avaloirViewModel)
        val location = avaloirViewModel.userCurrentLatLng.value

        LaunchedEffect(location) {

        }

        ContentSearch(navController, latLng = location)
    }

    @Composable
    fun ContentSearch(
        navController: NavController,
        latLng: LatLng
    ) {
        Log.d("ZEZE", "searchScreen location {$latLng")

        Column {

            LocationText(latLng)

            if (latLng.latitude > 0.0) {
                LaunchedEffect(true) {
                    Log.d("ZEZE", "searchScreen searching {$latLng")
                    avaloirViewModel.search(latLng.latitude, latLng.longitude, "100m")
                }
                ResultSearch(avaloirViewModel, navController)
            }
        }
    }

    @Composable
    fun ResultSearch(
        avaloirViewModel: AvaloirViewModel= viewModel(),
        navController: NavController
    ) {
        val content = remember { mutableStateOf("Home Screen") }

        Log.d("ZEZE", "searchScreen resultsearch")
        when (val state = avaloirViewModel.resultSearch.collectAsState().value) {
            is SearchResponseUiState.Loading -> {
                LoadScreen()
            }
            is SearchResponseUiState.Error -> {
                Log.d("ZEZE", "error")
                ErrorDialog(state.message)
            }
            is SearchResponseUiState.Loaded -> {
                Log.d("ZEZE", "loaded")
                Log.d("ZEZE", "search avaloirs ${state.response}")
                LoadAvaloirs(state.response.avaloirs, navController)
            }
            else -> {

            }
        }
    }

    @Composable
    fun LocationText(location: LatLng?) {
        if (location != null) {
            Text(text = "Votre localisation: ${location.latitude}, ${location.longitude}")
        } else {
            Text(text = "Pas de localisation")
        }
    }

    @Composable
    fun LoadScreen() {
        Text(text = "Recherche en cours...")
        CircularProgressIndicator(progress = 0.5f)
        Log.d("ZEZE", "loading")
    }
}