package be.marche.apptravaux.screens.avaloir

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.CreateFileState
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.ErrorDialog
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.utils.DateUtils
import be.marche.apptravaux.utils.FileHelper
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.io.File

class AvaloirPhotoScreen(
    val navController: NavController,
) {
    val fileHelper = FileHelper()

    @ExperimentalMaterialApi
    @Composable
    fun TakePicureMain(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        LaunchedEffect(true) {
            avaloirViewModel.createFileForSaving()
        }

        val resultStateTakePhoto = rememberSaveable {
            mutableStateOf(false)
        }

        Scaffold(
            topBar = {
                TopAppBarJf(
                    stringResource(R.string.add_photo_avaloir)
                ) { navController.navigate(TravauxRoutes.AvaloirHomeScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                when (val state = avaloirViewModel.resultCreateFile.collectAsState().value) {
                    is CreateFileState.Error -> {
                        ErrorDialog(state.message)
                    }
                    is CreateFileState.Success -> {
                        TakePicureContent(state.file, resultStateTakePhoto, avaloirViewModel)
                    }
                    else -> {
                        ErrorDialog("Erreur inconnue")
                    }
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun TakePicureContent(
        fileImage: File,
        resultStateTakePhoto: MutableState<Boolean>,
        avaloirViewModel: AvaloirViewModel
    ) {
        val context = LocalContext.current
        val fileUri = fileHelper.createUri(context, fileImage)
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { result: Boolean ->
            if (result) {
                resultStateTakePhoto.value = true
            }
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                cameraLauncher.launch(fileUri)
            } else {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }

        val location = rememberSaveable { avaloirViewModel.currentLatLng }

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ImagePreviewFromUri(resultStateTakePhoto.value, fileUri)
            LocationText(location)
            Text(
                text = stringResource(R.string.photo_tip),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            BtnTake(permissionLauncher, cameraLauncher, fileUri)
            BtnConfirm(resultStateTakePhoto.value, fileImage, avaloirViewModel, location, context)
            BtnCancel()
        }
    }

    @Composable
    private fun BtnTake(
        permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
        cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
        fileUri: Uri
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
                            cameraLauncher.launch(fileUri)
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
    private fun ImagePreviewFromUri(
        statePhoto: Boolean,
        uri: Uri
    ) {
        if (statePhoto) {
            Image(
                rememberAsyncImagePainter(uri),
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
        statePhoto: Boolean,
        fileImage: File,
        avaloirViewModel: AvaloirViewModel,
        location: LatLng,
        context: Context
    ) {
        Button(
            onClick = {
                val avaloir =
                    Avaloir(
                        id = null,
                        idReferent = 0,
                        cosLatitude = 0.0,
                        cosLongitude = 0.0,
                        sinLatitude = 0.0,
                        sinLongitude = 0.0,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        imageUrl = fileImage.path,
                        createdAt = DateUtils.dateToday(true)
                    )
                avaloirViewModel.insertAvaloir(avaloir)
                Toast.makeText(context, "Avaloir ajouté", Toast.LENGTH_SHORT).show()
                navController.navigate(TravauxRoutes.AvaloirListScreen.route) {
                    popUpTo(TravauxRoutes.AvaloirHomeScreen.route)
                }
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

    @Composable
    private fun BtnCancel(
    ) {
        Button(
            onClick = {
                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
            },
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Red)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.cancel),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }

    @Composable
    private fun LocationText(location: LatLng?) {
        if (location != null) {
            Text(text = "Votre localisation: ${location.latitude}, ${location.longitude}")
        } else {
            Text(text = "Pas de localisation")
        }
    }
}