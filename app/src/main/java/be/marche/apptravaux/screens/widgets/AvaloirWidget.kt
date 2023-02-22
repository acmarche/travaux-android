package be.marche.apptravaux.screens.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.networking.ConnectionState
import be.marche.apptravaux.networking.connectivityState
import be.marche.apptravaux.ui.theme.ScreenSizeTheme
import be.marche.apptravaux.utils.DateUtils.Companion.formatDateTime
import be.marche.apptravaux.utils.DownloadHelper
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.util.*

class AvaloirWidget {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    fun LoadAvaloirs(
        avaloirs: List<Avaloir>,
        searchState: MutableState<TextFieldValue>?,
        navController: NavController
    ) {
        val context = LocalContext.current
        val connection by connectivityState()
        val isConnected = connection == ConnectionState.Available
        val downloadHelper = DownloadHelper(context)

        LazyColumn {
            val filteredAvaloirs: List<Avaloir>
            val searchedText = searchState?.value?.text

            filteredAvaloirs = when {
                searchedText == null -> avaloirs
                searchedText.isEmpty() -> avaloirs
                else -> {
                    val resultList = ArrayList<Avaloir>()
                    for (avaloir in avaloirs) {
                        if (filterAvaloir(avaloir, searchedText)) {
                            resultList.add(avaloir)
                        }
                    }
                    resultList
                }
            }
            items(filteredAvaloirs) { avaloir ->
                ItemAvaloir(avaloir, isConnected, downloadHelper) {
                    navController.navigate(TravauxRoutes.AvaloirDetailScreen.route + "/${avaloir.idReferent}")
                }
            }
        }
    }

    private fun filterAvaloir(avaloir: Avaloir, searchText: String): Boolean {
        if (searchText.isEmpty())
            return true
        if (avaloir.rue == null)
            return true

        return avaloir.rue.lowercase(Locale.getDefault())
            .contains(searchText.lowercase(Locale.getDefault()))
    }

    @Composable
    fun ItemAvaloir(
        avaloir: Avaloir,
        isConnected: Boolean,
        downloadHelper: DownloadHelper,
        onItemCLick: (Int) -> Unit
    ) {
        val imgPath = this.ImageAvaloirPath(avaloir, isConnected, downloadHelper)

        Card(
            modifier = Modifier
                .clickable {
                    onItemCLick(avaloir.idReferent)
                }
                .padding(10.dp)
                .fillMaxSize(),
            elevation = 5.dp,
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ImageAvaloir(
                    imgPath,
                    ScreenSizeTheme.dimens.width,
                    ScreenSizeTheme.dimens.height
                )
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    val texteRue = avaloir.rue ?: "non déterminé"
                    Text(
                        text = texteRue,
                        style = ScreenSizeTheme.textStyle.fontStyle_1,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(5.dp))
                    val texteLocalite = avaloir.localite ?: "non déterminé"

                    Text(
                        text = texteLocalite,
                        style = ScreenSizeTheme.textStyle.fontStyle_1,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "Ajouté le ${formatDateTime(avaloir.createdAt)}",
                        style = ScreenSizeTheme.textStyle.fontStyle_1,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(5.dp))
                }
            }
        }
    }

    fun ImageAvaloirPath(
        avaloir: Avaloir,
        isConnected: Boolean,
        downloadHelper: DownloadHelper
    ): String? {

        if (isConnected) {
            if (avaloir.imageUrl !== null) {
                return avaloir.imageUrl!!
            }
        }

        if (avaloir.idReferent == 0) {
            return avaloir.imageUrl
        }

        val imagePath = downloadHelper.imageFullPath(avaloir.idReferent)

        if (File(imagePath).canRead()) {
            return imagePath
        }

        return null
    }

    @Composable
    fun ImageAvaloir(
        imagePath: String?,
        imageWidth: Dp,
        imageHeight: Dp,
        contentScale: ContentScale = ContentScale.Crop,
        padding: Dp = 5.dp
    ) {
        val painterImg: Painter
        if (imagePath == null) {
            painterImg = painterResource(R.drawable.profile_picture)
        } else {
            painterImg = rememberAsyncImagePainter(imagePath)
        }

        Image(
            painterImg,
            contentDescription = "Image",
            contentScale = contentScale,
            modifier = Modifier
                .width(imageWidth)
                .height(imageHeight)
                .padding(padding)
        )
    }
}