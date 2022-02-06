package com.myricseptember.countryfactcomposefinal.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.marche.apptravaux.entities.Avaloir
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import be.marche.apptravaux.R
import be.marche.apptravaux.screens.CardData
import be.marche.apptravaux.ui.theme.green
import be.marche.apptravaux.ui.theme.red
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@Composable
fun AvaloirRow(
    avaloir: Avaloir,
    onItemCLick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(130.dp)
            .clickable {
                onItemCLick(avaloir.idReferent)
            },
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = 6.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            avaloir.localite?.let { Text(text = it) }
        }
    }
}

@Composable
fun CardRow(
    data: CardData,
    onItemCLick: (String) ->
    Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(130.dp)
            .clickable {
                onItemCLick(data.url)
            },
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = 6.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = data.texte)
        }
    }
}

@Composable
fun ErrorDialog(message: String) {
    Text(
        text = message,
        modifier = Modifier
            .padding(start = 24.dp, top = 50.dp, end = 24.dp, bottom = 50.dp),
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
    )
}

//call  onClick = {
//                    openDialog.value = true
//                },
@Composable
fun FloatAlertDialog(openDialog: MutableState<Boolean>) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Floating Action", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "Let's Start...")
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(text = "Ok")
                }
            }
        )
    }
}


@Composable
fun ConnectivityStatusBox(
    isConnected: Boolean
) {
    val backgroundColor by animateColorAsState(targetValue = if (isConnected) green else red)
    val message = if (isConnected) "Back Online!" else "No Internet Connection!"
    val iconResource = if (isConnected) {
        R.drawable.ic_connectivity_available
    } else {
        R.drawable.ic_connectivity_unavailable
    }
    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconResource),
                contentDescription = "Connection Image",
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun ImageCoil() {
    val painter = rememberImagePainter(
        data = "https://picsum.photos/300/300",
        builder = {
            crossfade(true)
        }
    )

    Box {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.app_name),
        )

        when (painter.state) {
            is ImagePainter.State.Loading -> {
                // Display a circular progress indicator whilst loading
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            is ImagePainter.State.Error -> {
                // If you wish to display some content if the request fails
            }
        }
    }
}