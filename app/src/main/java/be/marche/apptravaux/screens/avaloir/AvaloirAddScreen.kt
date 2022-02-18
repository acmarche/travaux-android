package be.marche.apptravaux.screens.avaloir

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import be.marche.apptravaux.AvaloirAddActivity
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.SearchResponseUiState
import be.marche.apptravaux.ui.entities.SearchResponse
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.AvaloirViewModel
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
    fun SearchScreen(location: Location?, searchResponseUiState: SearchResponseUiState) {


        when {
            is SearchResponseUiState.loading -> Text()
            searchResponseUiState.message -> Text()
        }


        CircularProgressIndicator(progress = 0.5f)
        val t =
            AvaloirAddScreen(avaloirViewModel = avaloirViewModel)
        t.LocationScreen(location)
        LoadAvaloirs(avaloirs = avaloirs, navController)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun LocationScreen(location: Location?) {
        if (location != null) {
            Text(text = "Ma location ${location.latitude}, ${location.longitude}")
        } else {
            Text(text = "Pas de location")
        }
    }
}