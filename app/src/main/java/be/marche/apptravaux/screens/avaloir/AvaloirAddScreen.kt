package be.marche.apptravaux.screens.avaloir

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.ui.theme.Colors.Pink500
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.utils.FileHelper
import be.marche.apptravaux.viewModel.AvaloirViewModel
import coil.compose.rememberImagePainter
import com.google.android.libraries.maps.model.LatLng
import com.myricseptember.countryfactcomposefinal.widgets.ErrorDialog
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.URI


class AvaloirAddScreen(
    val avaloirViewModel: AvaloirViewModel
) {
    val fileHelper = FileHelper()
    var fileImage: File? = null

    object MyBitmap {
        var bitmap: Bitmap? = null
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun AddScreenMain(
        avaloirViewModel: AvaloirViewModel = viewModel(),
        navController: NavController
    ) {
        val t = File("/storage/emulated/0/Android/data/be.marche.apptravaux/files/Pictures/avaloir_zeze.jpg")
        Log.d("ZEZE", "file size ${t.length()}")
        Log.d("ZEZE", "addScreen main")
        val location = avaloirViewModel.userCurrentLatLng.value
        Log.d("ZEZE", "addScreen location $location")
        AppTravaux6Theme {
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
                Image(
                    rememberImagePainter(t),
                    contentDescription = "...",
                )
                //ContentMainScreen(navController, location)
            }
        }
    }

    @Composable
    fun ContentMainScreen(
        navController: NavController,
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
    fun TakePicure(
        avaloirViewModel: AvaloirViewModel = viewModel(),
        navController: NavController
    ) {
        Log.d("ZEZE", "take picture")

        val statePhoto = remember {
            mutableStateOf(false)
        }

        val context = LocalContext.current
        try {
            fileImage = fileHelper.createImageFile(context)
        } catch (io: IOException) {

        }

        if (fileImage != null) {
            val uri = fileHelper.createUri(context, fileImage!!)
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { result: Boolean ->
                if (result) {
                    Log.d("ZEZE", " oki camera $uri")
                    statePhoto.value = true
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
            ImageFromUri(navController, statePhoto, uri)
            Content(permissionLauncher, cameraLauncher, uri)
        }

        when (val state = avaloirViewModel.resultCreateFile.collectAsState().value) {
            is CreateFileState.Error -> {
                ErrorDialog(state.message)
            }
            else -> {

            }
        }


    }

    private @Composable
    fun ImageFromUri(
        navController: NavController, statePhoto: MutableState<Boolean>, uri: Uri
    ) {
        val context = LocalContext.current
        if (statePhoto.value) {
            val fileToSave = File(uri.path)

            Log.e("ZEZE", "load img uri $uri")
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
            } catch (e: IOException) {
                Log.d("ZEZE", "error bitmap ${e.message}")
                e.printStackTrace();
            }
            if (bitmap != null) {
                Log.d("ZEZE", "ok bitmap")
                if (fileImage != null)
                    fileHelper.bitmapToFile(bitmap, fileImage!!)

                fileHelper.saveBitmap(bitmap, fileImage!!)

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Image",
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.45f)
                        .padding(top = 10.dp),
                    contentScale = ContentScale.Fit
                )
                BtnConfirm(navController)
            }
        }
    }

    @Composable
    fun Content(
        permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
        cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
        uri: Uri
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.photo_tip),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) -> {
                                Log.e("ZEZE", "launch camera ${uri.path}")
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
    }

    @Composable
    fun BtnConfirm(
        navController: NavController
    ) {
        MyBitmap.bitmap?.let { btm ->
            Button(
                onClick = {
                    navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
                },
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

/*
            val bmOptions = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(it.getAbsolutePath(), bmOptions)
 */
        /*
        MyBitmap.bitmap?.let { btm ->
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
        }*/
    }
}