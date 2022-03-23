package be.marche.apptravaux.screens.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import be.marche.apptravaux.ui.theme.Colors

@Composable
fun TopAppBarJf(texte: String, onCLickAction: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = texte,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onCLickAction
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
        },
        backgroundColor = Colors.Pink500,
        elevation = AppBarDefaults.TopAppBarElevation
    )
}