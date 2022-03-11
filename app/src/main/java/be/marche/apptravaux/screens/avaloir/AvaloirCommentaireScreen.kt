package be.marche.apptravaux.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel
import timber.log.Timber
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
            Timber.d(" effet ${avaloirId}")
            if (avaloirId != null) {
                avaloirViewModel.getSelectedAvaloir(avaloirId)
            }
        }

        val selectedAvaloir by avaloirViewModel.selectedAvaloir.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Ajout d'un commentaire",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.AvaloirDetailScreen.route + "/${avaloirId}")
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
                    DetailContentScreen(it)
                } else {
                    Text(text = "Avaloir non trouvé")
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
        val timeStamp = Date()
        val commentaire = Commentaire(null, 0, avaloir.idReferent, content, timeStamp)

        avaloirViewModel.insertCommentaireDb(commentaire)
    }


}