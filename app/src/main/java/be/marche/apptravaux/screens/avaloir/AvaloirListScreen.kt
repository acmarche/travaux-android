package be.marche.apptravaux.screens.avaloir

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.AvaloirUiState
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.AvaloirWidget
import be.marche.apptravaux.screens.widgets.ErrorDialog
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.ui.theme.ScreenSizeTheme
import be.marche.apptravaux.viewModel.AvaloirViewModel

class AvaloirListScreen(val navController: NavController) {

    @Composable
    fun ListScreen(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        LaunchedEffect(true) {
            avaloirViewModel.fetchAvaloirsFromDb()
        }

        val textState = remember { mutableStateOf(TextFieldValue("")) }

        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Liste des avaloirs"
                ) { navController.navigate(TravauxRoutes.AvaloirHomeScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

                when (val state = avaloirViewModel.uiState.collectAsState().value) {
                    is AvaloirUiState.Loading -> {
                    }
                    is AvaloirUiState.Error -> {
                        ErrorDialog(state.message)
                    }
                    is AvaloirUiState.Loaded -> {
                        val widget = AvaloirWidget()
                        Column {
                            SearchView(textState, {})
                            widget.LoadAvaloirs(state.data, textState, navController)
                        }
                    }
                    is AvaloirUiState.Empty -> {
                        Column {
                            ErrorDialog("La liste est vide")
                            Divider(
                                modifier = Modifier.height(MEDIUM_PADDING),
                                color = MaterialTheme.colors.background
                            )

                            Button(
                                onClick = { navController.navigate(TravauxRoutes.SyncScreen.route) }
                            ) {
                                Text(text = "Synchroniser les données")
                            }
                            Divider(
                                modifier = Modifier.height(MEDIUM_PADDING),
                                color = MaterialTheme.colors.background
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun SearchView(
        state: MutableState<TextFieldValue>,
        onChange: (TextFieldValue) -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = state.value,
            label = { Text(text = "Rue") },
            onValueChange = { value ->
                state.value = value
                //onChange(value)
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            textStyle = ScreenSizeTheme.textStyle.fontStyleSearch,
            // textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(15.dp)
                        .size(24.dp)
                )
            },
            trailingIcon = {
                if (state.value != TextFieldValue("")) {
                    IconButton(
                        onClick = {
                            state.value =
                                TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(15.dp)
                                .size(24.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RectangleShape,
        )
    }


}