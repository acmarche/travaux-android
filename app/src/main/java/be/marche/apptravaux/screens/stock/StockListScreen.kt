package be.marche.apptravaux.screens.stock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.Produit
import be.marche.apptravaux.entities.ProduitUiState
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.ErrorDialog
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.StockViewModel

class StockListScreen(val navController: NavController) {

    @Composable
    fun ListScreen(
        stockViewModel: StockViewModel = viewModel()
    ) {
        LaunchedEffect(true) {
            stockViewModel.fetchProduitsFromDb()
        }

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
                                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
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
                }
                is ProduitUiState.Empty -> {
                    Column {
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

    @Composable
    fun LoadProduits(
        produits: List<Produit>,
        navController: NavController
    ) {
        LazyColumn {
            items(produits) { produit ->
                ItemProduit(produit) {
                    navController.navigate(TravauxRoutes.AvaloirDetailScreen.route + "/${produit.id}")
                }
            }
        }
    }

    @Composable
    fun ItemProduit(
        produit: Produit,
        onItemCLick: (Int) -> Unit
    ) {
        Card(
            modifier = Modifier
                .clickable {
                    onItemCLick(produit.id)
                }
                .padding(10.dp)
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    Text(
                        text = "Quantité: ${produit.quantite}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }


}