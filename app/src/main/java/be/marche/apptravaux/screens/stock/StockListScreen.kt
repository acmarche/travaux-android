package be.marche.apptravaux.screens.stock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Categorie
import be.marche.apptravaux.entities.Produit
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.MyNumberField
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.viewModel.StockViewModel
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.util.*

class StockListScreen(val navController: NavController, val stockViewModel: StockViewModel) {

    var selectedCategorie = mutableStateOf(0)
    val textState = mutableStateOf(TextFieldValue(""))

    @Composable
    fun ListScreen(
        //  stockViewModel: StockViewModel = viewModel()
    ) {
        LaunchedEffect(true) {
            stockViewModel.fetchProduitsFromDb(0, null)
            stockViewModel.fetchCategoriesFromDb()
        }

        Timber.d("create ListScreen")
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        val categorieState = remember { mutableStateOf(0) }

        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Liste des produits"
                ) { navController.navigate(TravauxRoutes.StockHomeScreen.route) }
            }
        ) {
            Column {
                SearchView(textState, {})
                ListSelectCategories(categorieState, { onSelectCategorie(it) })
                CountryList(navController, categorieState, textState)
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
                //ListSelectCategories(categories, { onSelectCategorie(it) })
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
    fun CountryList(
        navController: NavController,
        categorieState: MutableState<Int>,
        searchState: MutableState<TextFieldValue>
    ) {
        Timber.d("country list cat ${categorieState.value} et ${searchState.value}")
        val produits = stockViewModel.allProduits
        var filteredProduits: List<Produit>
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            val searchedText = searchState.value.text
            val searchedCategorie = categorieState.value
            filteredProduits = if (searchedText.isEmpty() && searchedCategorie == 0) {
                produits
            } else {
                val resultList = ArrayList<Produit>()
                for (produit in produits) {
                    if (filterProduit(produit, searchedCategorie, searchedText)) {
                        resultList.add(produit)
                    }
                }
                resultList
            }
            items(filteredProduits) { filteredProduit ->
                ItemProduit(filteredProduit, { }, {})
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun SearchView(
        state: MutableState<TextFieldValue>,
        onChange: (TextFieldValue) -> Unit
    ) {
        Timber.d("create SearchView")
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = state.value,
            label = { Text(text = "Mot clef") },
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
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
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
            /*  colors = TextFieldDefaults.textFieldColors(
                  textColor = Color.White,
                  cursorColor = Color.White,
                  leadingIconColor = Color.White,
                  trailingIconColor = Color.White,
                  backgroundColor = Color.Transparent,
                  focusedIndicatorColor = Color.Transparent,
                  unfocusedIndicatorColor = Color.Transparent,
                  disabledIndicatorColor = Color.Transparent
              )*/
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ListSelectCategories(
        categorieState: MutableState<Int>,
        onChange: (Int) -> Unit
    ) {
        Timber.d("create ListSelectCategories")
        var expanded by remember { mutableStateOf(false) }
        var firstElement by remember { mutableStateOf(Categorie(0, "Toutes les categories", "")) }
        val categories = stockViewModel.allCategories

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
                categories.forEach { categorie ->
                    DropdownMenuItem(
                        onClick = {
                            Timber.d("click cat ${categorie.id}")
                            categorieState.value = categorie.id
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

    private fun filterProduit(produit: Produit, categorieId: Int, searchText: String): Boolean {
        return checkCat(produit, categorieId) && checkKeyword(produit, searchText)
    }

    private fun checkCat(produit: Produit, categorieId: Int): Boolean {
        Timber.d("checkCat $categorieId, ${produit.categorie_id}")
        if (categorieId == 0)
            return true

        return produit.categorie_id == categorieId
    }

    private fun checkKeyword(produit: Produit, searchText: String): Boolean {
        Timber.d("checkKeyword $searchText, $produit.nom")
        if (searchText.length == 0)
            return true
        return produit.nom.lowercase(Locale.getDefault())
            .contains(searchText.lowercase(Locale.getDefault()))
    }

    private fun onSelectCategorie(categorieId: Int) {
        //  selectedCategorie.value = categorieId
        //  stockViewModel.fetchProduitsFromDb(selectedCategorie.value, textState.value.text)
    }

    private fun onSearchText(textSearched: TextFieldValue) {
        //textState.value = textSearched
        Timber.d("on search ${textSearched.text}")
        //   stockViewModel.fetchProduitsFromDb(selectedCategorie.value, textState.value.text)
    }

}