package be.marche.apptravaux.screens.widgets

import android.content.Context
import android.icu.text.DateFormat
import android.net.Uri
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import be.marche.apptravaux.ui.theme.ScreenSizeTheme
import be.marche.apptravaux.utils.FileHelper
import coil.compose.rememberImagePainter
import java.io.File
import java.util.*

class AvaloirWidget {

    val fileHelper = FileHelper()

    @Composable
    fun LoadAvaloirs(
        avaloirs: List<Avaloir>,
        searchState: MutableState<TextFieldValue>?,
        navController: NavController
    ) {
        val context = LocalContext.current
        LazyColumn {
            val filteredAvaloirs: List<Avaloir>
            val searchedText = searchState?.value?.text

            when {
                searchedText == null -> filteredAvaloirs = avaloirs
                searchedText.isEmpty() -> filteredAvaloirs = avaloirs
                else -> {
                    val resultList = ArrayList<Avaloir>()
                    for (avaloir in avaloirs) {
                        if (filterAvaloir(avaloir, searchedText)) {
                            resultList.add(avaloir)
                        }
                    }
                    filteredAvaloirs = resultList
                }
            }
            items(filteredAvaloirs) { avaloir ->
                ItemAvaloir(avaloir, context) {
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
        context: Context,
        onItemCLick: (Int) -> Unit
    ) {
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
                    avaloir,
                    context,
                    ScreenSizeTheme.dimens.width,
                    ScreenSizeTheme.dimens.height
                )
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    val texteRue = avaloir.rue ?: "non déterminé"
                    Text(
                        text = "Rue: $texteRue",
                        style = ScreenSizeTheme.textStyle.fontStyle_1,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(5.dp))
                    val texteLocalite = avaloir.localite ?: "non déterminé"

                    Text(
                        text = "Localité: $texteLocalite",
                        style = ScreenSizeTheme.textStyle.fontStyle_1,
                        fontWeight = FontWeight.Normal
                    )

                    Text(
                        text = "Ajouté le ${formatDate(avaloir.createdAt)}",
                        style = ScreenSizeTheme.textStyle.fontStyle_1,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(5.dp))
                }
            }
        }
    }

    fun formatDate(createdAt: Date): String {
        //val format = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        return DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_DAY + DateFormat.HOUR24_MINUTE).format(createdAt)
    }

    @Composable
    fun ImageAvaloir(
        avaloir: Avaloir,
        context: Context,
        imageWidth: Dp,
        imageHeight: Dp,
        contentScale: ContentScale = ContentScale.Crop,
        padding: Dp = 5.dp
    ) {
        with(avaloir.imageUrl) {
            when {
                this == null -> {
                    Image(
                        painterResource(R.drawable.profile_picture),
                        contentDescription = "Image",
                        contentScale = contentScale,
                        // contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .width(imageWidth)
                            .height(imageHeight)
                            .padding(padding)
                    )
                }
                this.contains("http") -> {
                    Image(
                        painter = rememberImagePainter(avaloir.imageUrl),
                        contentDescription = "Image",
                        modifier = Modifier
                            .width(imageWidth)
                            .height(imageHeight)
                            .padding(padding)
                            .clip(RoundedCornerShape(5.dp))
                    )
                }
                else -> {
                    var fileUri: Uri? = null
                    try {
                        val cacheFile = File(avaloir.imageUrl!!)
                        fileUri = fileHelper.createUri(context, cacheFile)
                    } catch (e: Exception) {

                    }
                    if (fileUri != null) {
                        Image(
                            rememberImagePainter(fileUri),
                            contentDescription = "Image",
                            contentScale = contentScale,
                            modifier = Modifier
                                .width(imageWidth)
                                .height(imageHeight)
                                .padding(padding)
                        )
                    }
                }
            }
        }
    }
}