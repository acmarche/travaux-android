package be.marche.apptravaux.screens.stock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Categorie
import be.marche.apptravaux.entities.Produit
import be.marche.apptravaux.entities.ProduitUiState
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.ErrorDialog
import be.marche.apptravaux.screens.widgets.MyNumberField
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.StockViewModel
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class StockListScreen(val navController: NavController, val stockViewModel: StockViewModel) {

    var selectedCategorie = mutableStateOf(0)
    val textState = mutableStateOf(TextFieldValue(""))

    @Composable
    fun ListScreen(
        //  stockViewModel: StockViewModel = viewModel()
    ) {
        LaunchedEffect(true) {
            stockViewModel.fetchProduitsFromDb(0,null)
        }

        Timber.d("create ListScreen")

        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Liste des produits"
                ) { navController.navigate(TravauxRoutes.StockHomeScreen.route) }
            }
        ) {
            when (val state = stockViewModel.produitsUiState.collectAsState().value) {
                is ProduitUiState.Loading -> {
                }
                is ProduitUiState.Error -> {
                    ErrorDialog(state.message)
                }
                is ProduitUiState.Loaded -> {
                    LoadProduits(state.data, navController)
                    stockViewModel.fetchCategoriesFromDb()
                }
                is ProduitUiState.Empty -> {
                    Column {
                        if (selectedCategorie.value > 0) {
                            Text(
                                text = "Pas de produits suivant la recherche",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            LoadProduits(emptyList(), navController)
                        } else {
                            ErrorDialog("La liste est vide")
                            Divider(
                                modifier = Modifier.height(MEDIUM_PADDING),
                                color = MaterialTheme.colors.background
                            )
                            Button(
                                onClick = { navController.navigate(TravauxRoutes.StockSyncScreen.route) }
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

    @Composable
    fun LoadProduits(
        produits: List<Produit>,
        navController: NavController
    ) {
        Timber.d("create LoadProduits")
        var selectedCategorie2 by remember { mutableStateOf<Categorie?>(null) }
        var keyword by remember { mutableStateOf<String?>(null) }
        var expanded by remember { mutableStateOf<Boolean>(false) }
        val categories = stockViewModel.allCategories

        LazyColumn(modifier = Modifier.padding(10.dp)) {
            item {
                SearchView(textState, { onSearchText(it) })
            }
            item {
                ListSelectCategories(categories, { onSelectCategorie(it) })
            }
            /*  item {
                  CountryTextField(
                      label = "Select Country",
                      modifier = Modifier
                          .padding(top = 50.dp),
                      // .align(Alignment.TopCenter),
                      expanded = expanded,
                      selectedCategorie = selectedCategorie
                  ) {
                      expanded = !expanded
                  }
              }*/
            items(produits) { produit ->
                ItemProduit(
                    produit,
                    { },
                    {
                        changeQuantite(produit, it)
                    }
                )
            }
        }
    }

    @Composable
    fun ItemProduit(
        produit: Produit,
        onItemCLick: (Int) -> Unit,
        onChange: (String) -> Unit
    ) {
        Timber.d("create ItemProduit")
        Card(
            modifier = Modifier
                .clickable {
                    onItemCLick(produit.id)
                }
                //    .padding(10.dp)
                .fillMaxSize(),
            elevation = 5.dp,
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = produit.nom,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    MyNumberField("${produit.quantite}", onChange)

                    Spacer(modifier = Modifier.padding(5.dp))

                    Text(
                        text = "Catégorie: ${produit.categorieName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }

    @Composable
    fun SearchView(
        state: MutableState<TextFieldValue>,
        onChange: (TextFieldValue) -> Unit
    ) {
        Timber.d("create SearchView")
        TextField(
            value = state.value,
            onValueChange = { value ->
                state.value = value
                onChange(value)
            },
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
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
            shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                cursorColor = Color.White,
                leadingIconColor = Color.White,
                trailingIconColor = Color.White,
                backgroundColor = colorResource(id = R.color.purple_500),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ListSelectCategories(
        options: List<Categorie>,
        onChange: (Int) -> Unit
    ) {
        Timber.d("create ListSelectCategories")
        var expanded by remember { mutableStateOf(false) }
        var firstElement by remember { mutableStateOf(Categorie(0, "Toutes les categories", "")) }

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

    private fun changeQuantite(produit: Produit, quantite: String) {
        try {
            val quantiteInt = quantite.toInt()
            if (quantiteInt > -1) {
                produit.quantite = quantiteInt
                stockViewModel.upDateQuantite(produit)
                stockViewModel.updateQuantiteDraft(produit.nom, produit.id, quantiteInt)
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }
    }

    private fun onSelectCategorie(categorieId: Int) {
        selectedCategorie.value = categorieId
        stockViewModel.fetchProduitsFromDb(selectedCategorie.value, textState.value.text)
    }

    private fun onSearchText(textSearched: TextFieldValue) {
        //textState.value = textSearched
        Timber.d("on search ${textSearched.text}")
        stockViewModel.fetchProduitsFromDb(selectedCategorie.value, textState.value.text)
    }

}