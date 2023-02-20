package be.marche.apptravaux.screens

import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.BuildConfig
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.ErrorLog
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.ScreenSizeTheme
import be.marche.apptravaux.utils.DownloadHelper
import be.marche.apptravaux.utils.FileHelper
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.viewModel.ErrorViewModel
import be.marche.apptravaux.viewModel.StockViewModel
import timber.log.Timber
import java.io.File

class SettingScreen(val navController: NavController) {

    @Composable
    fun MainScreen(
        errorViewModel: ErrorViewModel,
        avaloirViewModel: AvaloirViewModel,
        stockViewModel: StockViewModel
    ) {
        val context = LocalContext.current
        val a = CardData(
            "Local data"
        ) {
            Toast.makeText(
                context,
                "Non implémenté",
                Toast.LENGTH_LONG
            ).show()
        }

        val errors = errorViewModel.allErrorsFlow.collectAsState().value
        Content(datas = errors, errorViewModel, avaloirViewModel, stockViewModel)
    }

    @Composable
    private fun Content(
        datas: List<ErrorLog>,
        errorViewModel: ErrorViewModel,
        avaloirViewModel: AvaloirViewModel,
        stockViewModel: StockViewModel
    ) {
        Scaffold(
            topBar = {
                TopAppBarJf("Version et logs") {
                    navController.navigate(TravauxRoutes.HomeScreen.route)
                }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                val context = LocalContext.current
                val versionCode: Int = BuildConfig.VERSION_CODE
                val versionName: String = BuildConfig.VERSION_NAME

                val d = DownloadHelper(context)
                d.listFiles()

                val version: String = java.lang.String.format(
                    stringResource(R.string.app_version),
                    versionCode,
                    versionName
                )

                LaunchedEffect(true) {
                    avaloirViewModel.countAvaloirs()
                    stockViewModel.countProduit()
                }

                val statAvaloirs: String = java.lang.String.format(
                    stringResource(R.string.stat_avaloirs),
                    avaloirViewModel._countAvaloirs,
                )

                val downloadHelper = DownloadHelper(context)
                val filesCount = downloadHelper.countFiles()
                val statImages: String = java.lang.String.format(
                    stringResource(R.string.stat_images),
                    filesCount,
                )

                val statProduits: String = java.lang.String.format(
                    stringResource(R.string.stat_produits),
                    stockViewModel._countProduit,
                )

                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = version,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = statAvaloirs,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = statImages,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = statProduits,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    LazyColumn {
                        items(datas) { data ->
                            CardError(data.nom, data.description)
                        }
                    }
                    Button(
                        onClick = {
                            vider(errorViewModel)
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.vider),
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CardError(
        nom: String,
        description: String?
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
            elevation = 6.dp
        ) {
            Column(
            ) {
                Text(text = nom, fontSize = ScreenSizeTheme.textStyle.fontWidth_1)
                Text(text = "det: $description")
            }
        }
    }

    private fun vider(errorViewModel: ErrorViewModel) {
        errorViewModel.deleteAll()
    }
}