package be.marche.apptravaux.screens.stock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Categorie
import be.marche.apptravaux.entities.Produit
import be.marche.apptravaux.entities.ProduitUiState
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.ErrorDialog
import be.marche.apptravaux.screens.widgets.ListSelect
import be.marche.apptravaux.screens.widgets.MyNumberField
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.StockViewModel
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase


class StockListScreen(val navController: NavController, val stockViewModel: StockViewModel) {

    var selectedCategorie = mutableStateOf(0)

    @Composable
    fun ListScreen(
        //  stockViewModel: StockViewModel = viewModel()
    ) {
        LaunchedEffect(true) {
            stockViewModel.fetchProduitsFromDb(0)
        }

        //   selectedCategorie by remember { mutableStateOf<Categorie?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Liste des produits",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Left
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.StockHomeScreen.route)
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
        var selectedCategorie2 by remember { mutableStateOf<Categorie?>(null) }
        var keyword by remember { mutableStateOf<String?>(null) }
        var expanded by remember { mutableStateOf<Boolean>(false) }
        val categories = stockViewModel.allCategories

        LazyColumn(modifier = Modifier.padding(10.dp)) {
            item {
                ListSelect(categories, { onSelectCategorie(it) })
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
        stockViewModel.fetchProduitsFromDb(categorieId)
    }

}