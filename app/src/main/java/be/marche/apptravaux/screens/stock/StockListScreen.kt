package be.marche.apptravaux.screens.stock

import androidx.compose.foundation.background
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
import java.util.*

class StockListScreen(val navController: NavController, val stockViewModel: StockViewModel) {

    @Composable
    fun ListScreen(
        //  stockViewModel: StockViewModel = viewModel()
    ) {
        LaunchedEffect(true) {
            stockViewModel.fetchProduitsFromDb(0, null)
            stockViewModel.fetchCategoriesFromDb()
        }

        val textState = remember { mutableStateOf(TextFieldValue("")) }
        val categorieState = remember { mutableStateOf(0) }

        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Liste des produits"
                ) { navController.navigate(TravauxRoutes.StockHomeScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

                Column {
                    SearchView(textState, {})
                    ListSelectCategories(categorieState, { onSelectCategorie(it) })
                    ProduitsList(navController, categorieState, textState)
                }
            }
        }
    }

    @Composable
    fun ItemProduit(
        produit: Produit,
        onItemCLick: (Int) -> Unit,
        onChange: (String) -> Unit
    ) {
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
    fun ProduitsList(
        navController: NavController,
        categorieState: MutableState<Int>,
        searchState: MutableState<TextFieldValue>
    ) {
        val produits = stockViewModel.allProduits
        var filteredProduits: List<Produit>

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
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
                ItemProduit(filteredProduit, { }, { changeQuantite(filteredProduit, it) })
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
                .fillMaxWidth()
                .padding(10.dp),
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
            shape = RectangleShape,
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ListSelectCategories(
        categorieState: MutableState<Int>,
        onChange: (Int) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        var firstElement by remember { mutableStateOf(Categorie(0, "Toutes les categories", "")) }
        val categories = stockViewModel.allCategories

        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color.Transparent),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                readOnly = true,
                value = firstElement.nom,
                onValueChange = { },
                label = { Text("Catégories") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
            )
            ExposedDropdownMenu(
                modifier = Modifier,
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                categories.forEach { categorie ->
                    DropdownMenuItem(
                        onClick = {
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
        if (categorieId == 0)
            return true

        return produit.categorie_id == categorieId
    }

    private fun checkKeyword(produit: Produit, searchText: String): Boolean {
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
        //   stockViewModel.fetchProduitsFromDb(selectedCategorie.value, textState.value.text)
    }

}