package be.marche.apptravaux.screens.widgets

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Categorie
import be.marche.apptravaux.ui.theme.green
import be.marche.apptravaux.ui.theme.red
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter


@Composable
fun CardRow(
    texte: String,
    onItemCLick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(130.dp)
            .clickable {
                onItemCLick()
            },
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = 6.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = texte)
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

@Composable
fun ToastMessage(message: String) {
    val context = LocalContext.current
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
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
    val message = if (isConnected) "Connexion Internet ok" else "Pas de connexion Internet"
    val iconResource = if (isConnected) {
        R.drawable.ic_connectivity_available
    } else {
        R.drawable.ic_connectivity_unavailable
    }
    if (!isConnected) {
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

@Composable
fun MyScaffold(textTopBar: String, contentParam: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = textTopBar,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        content = contentParam
    )
}

@Composable
fun MyAppTopAppBar(topAppBarText: String, onBackPressed: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = topAppBarText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "rrrrr"
                )
            }
        },
        // ...
    )
}

@Composable
fun CircularProgressIndicatorSample() {
    val progress by remember { mutableStateOf(0.1f) }
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(30.dp))
        CircularProgressIndicator()
    }
}

@Composable
fun OutlinedTextFieldJf(textStateThree: MutableState<TextFieldValue>) {
    OutlinedTextField(
        value = textStateThree.value,
        onValueChange = { textStateThree.value = it },
        label = { Text(text = "Comment") },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        maxLines = 5,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        )
    )
}

@Composable
fun OutlinedButtonJf(texte: String, isEnabled: Boolean, onItemCLick: () -> Unit) {
    OutlinedButton(
        onClick = onItemCLick,
        shape = CircleShape,
        enabled = isEnabled,
        elevation = ButtonDefaults.elevation(8.dp),
        // modifier = Modifier.fillMaxWidth(0.5f)
    ) {
        Text(
            text = texte,
            modifier = Modifier.padding(6.dp)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MyNumberField(number: String, onChange: (String) -> Unit) {

    var inputValue by remember { mutableStateOf(number) }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = inputValue,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Quantité") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(
            onDone = {
                onChange(inputValue)
                keyboardController?.hide()
            }
        ),
        onValueChange = {
            inputValue = it
            //   onChange(it)
        }
        // keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListSelect(options: List<Categorie>, onChange: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var firstElement by remember { mutableStateOf(Categorie(0, "Toutes les categories","")) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = firstElement.nom,
            onValueChange = { },
            label = { Text("Catégories") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { categorie ->
                DropdownMenuItem(
                    onClick = {
                        onChange(categorie.id)
                        firstElement = categorie
                        expanded = false
                    }
                ) {
                    Text(text = categorie.nom)
                }
            }
        }
    }
}