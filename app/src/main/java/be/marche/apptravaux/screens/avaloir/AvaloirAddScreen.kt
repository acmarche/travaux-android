package be.marche.apptravaux.screens.avaloir

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.CreateFileState
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.GoogleMapWidget
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.Colors.Pink500
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.utils.FileHelper
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberImagePainter
import com.google.android.libraries.maps.model.LatLng
import com.myricseptember.countryfactcomposefinal.widgets.ErrorDialog
import kotlinx.coroutines.launch
import java.io.File


class AvaloirAddScreen(
    val navController: NavController,
) {
    val fileHelper = FileHelper()

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun AddScreenMain(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Log.d("ZEZE", "addScreen main")
        val location = avaloirViewModel.userCurrentLatLng.value
        Log.d("ZEZE", "addScreen location $location")
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.add_avaloir),
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
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    backgroundColor = Pink500,
                    elevation = AppBarDefaults.TopAppBarElevation
                )
            }
        ) {
            ContentMainScreen( location)
        }
    }

    @Composable
  private  fun ContentMainScreen(
        location: LatLng
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 25.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Déplacer la carte pour corriger la localisation",
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = { navController.navigate(TravauxRoutes.AvaloirPhotoScreen.route) }) {
                    Text("Valider et prendre une photo")
                }
                Button(onClick = {
                    navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
                }
                ) {
                    Text("Annuler")
                }
            }
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )
            GoogleMapWidget(
                location.latitude,
                location.longitude,
                null,
                true
            )
        }
    }

    @ExperimentalMaterialApi
    @Composable
    fun TakePicureMain(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Log.d("ZEZE", "take picture main")

        LaunchedEffect(true) {
            avaloirViewModel.createFileForSaving()
        }

        val resultStateTakePhoto = rememberSaveable {
            mutableStateOf(false)
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.add_photo_avaloir),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Colors.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
                            }
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    backgroundColor = Pink500,
                    elevation = AppBarDefaults.TopAppBarElevation
                )
            }
        ) {
            val state = avaloirViewModel.resultCreateFile.collectAsState().value

            when (state) {
                is CreateFileState.Error -> {
                    ErrorDialog(state.message)
                }
                is CreateFileState.Success -> {
                    TakePicureContent(state.file, resultStateTakePhoto, navController)
                }
                else -> {
                    ErrorDialog("Erreur inconnue")
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun TakePicureContent(
        fileImage: File,
        resultStateTakePhoto: MutableState<Boolean>,
        navController: NavController
    ) {
        Log.d("ZEZE", "take picture content")

        val context = LocalContext.current

        Log.d("ZEZE", " fileName ${fileImage.path}")

        val uri = fileHelper.createUri(context, fileImage)
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { result: Boolean ->
            if (result) {
                Log.d("ZEZE", " oki camera $uri")
                resultStateTakePhoto.value = true
            } else {
                Log.d("ZEZE", " KO camera $uri")
            }
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ImagePreviewFromUri(resultStateTakePhoto.value, uri)
            Text(
                text = stringResource(R.string.photo_tip),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            BtnTake(permissionLauncher, cameraLauncher, uri)
            BtnConfirm(resultStateTakePhoto.value)
        }
    }

    @Composable
  private   fun BtnTake(
        permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
        cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
        uri: Uri
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.CAMERA
                        ) -> {
                            cameraLauncher.launch(uri)
                        }
                        else -> {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.photo_take),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }

     @Composable
 private    fun ImagePreviewFromUri(
        statePhoto: Boolean,
        uri: Uri
    ) {
        if (statePhoto) {
            Log.d("ZEZE", "ok bitmap")
            Image(
                rememberImagePainter(uri),
                contentDescription = "Image",
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .padding(top = 3.dp),
                contentScale = ContentScale.Fit
            )
        }
    }

    @Composable
    private fun BtnConfirm(
        statePhoto: Boolean
    ) {
        Button(
            onClick = {
                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
            },
            enabled = statePhoto,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.confirm),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}