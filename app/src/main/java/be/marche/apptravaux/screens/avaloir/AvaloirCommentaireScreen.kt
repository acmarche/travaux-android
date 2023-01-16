package be.marche.apptravaux.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.OutlinedTextFieldJf
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.utils.DateUtils
import be.marche.apptravaux.viewModel.AvaloirViewModel
import java.time.LocalDateTime
import java.util.*

class AvaloirCommentaireScreen(
    val navController: NavController,
    val avaloirViewModel: AvaloirViewModel,
) {
    @Composable
    fun Main(
        avaloirId: Int?
    ) {
        LaunchedEffect(avaloirId) { // only launch once whenever cardId change
            if (avaloirId != null) {
                avaloirViewModel.getSelectedAvaloir(avaloirId)
            }
        }

        val selectedAvaloir by avaloirViewModel.selectedAvaloir.collectAsState()

        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Ajout d'un commentaire"
                ) { navController.navigate(TravauxRoutes.AvaloirDetailScreen.route + "/${avaloirId}") }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                selectedAvaloir.let {
                    if (it != null) {
                        DetailContentScreen(it)
                    } else {
                        Text(text = "Avaloir non trouvé")
                    }
                }
            }
        }
    }

    @Composable
    private fun DetailContentScreen(
        avaloir: Avaloir
    ) {
        val textStateThree = remember { mutableStateOf(TextFieldValue()) }
        Column {
            OutlinedTextFieldJf(textStateThree)
            Divider(
                modifier = Modifier.height(MEDIUM_PADDING),
                color = MaterialTheme.colors.background
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Top,
            ) {
                Button(
                    onClick = {
                        navController.navigate(TravauxRoutes.AvaloirDetailScreen.route + "/${avaloir.idReferent}")
                    },
                    modifier = Modifier
                        .background(Color.Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        addCommentaire(avaloir, textStateThree.value.text)
                        navController.navigate(TravauxRoutes.AvaloirDetailScreen.route + "/${avaloir.idReferent}")
                    },
                    modifier = Modifier
                        .background(Color.Green),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ajouter le commentaire",
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
    }

    private fun addCommentaire(avaloir: Avaloir, content: String) {

        val commentaire = Commentaire(null, 0, avaloir.idReferent, content, DateUtils.dateToday())

        avaloirViewModel.insertCommentaireDb(commentaire)
    }


}