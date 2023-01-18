package be.marche.apptravaux.screens.stock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.entities.QuantiteDraft
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.StockViewModel

class StockDraftScreen(
    val navController: NavController,
) {
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun MainScreen(
        stockViewModel: StockViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Données à envoyer au serveur"
                ) { navController.navigate(TravauxRoutes.StockHomeScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

                stockViewModel.refreshDrafts()
                val drafts =
                    stockViewModel.allQuantitesDraftsFlow.collectAsState()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${drafts.value.count()} données à envoyer",
                        style = MaterialTheme.typography.h5
                    )
                    Divider(
                        modifier = Modifier.height(MEDIUM_PADDING),
                        color = MaterialTheme.colors.background
                    )
                    Button(
                        onClick = {
                            navController.navigate(TravauxRoutes.SyncScreen.route)
                        }
                    ) {
                        Text(text = "Synchroniser les données")
                    }
                    Divider(
                        modifier = Modifier.height(MEDIUM_PADDING),
                        color = MaterialTheme.colors.background
                    )
                    FruitListAnimation(drafts.value, stockViewModel)
                }
            }
        }
    }

    @ExperimentalAnimationApi
    @Composable
    fun FruitListAnimation(
        quantiteDrafts: List<QuantiteDraft>,
        stockViewModel: StockViewModel
    ) {
        val deletedFruitList = remember { mutableStateListOf<QuantiteDraft>() }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(
                        items = quantiteDrafts,
                        itemContent = { _, quantiteDraft ->
                            AnimatedVisibility(
                                visible = !deletedFruitList.contains(quantiteDraft),
                                enter = expandVertically(),
                                exit = shrinkVertically(
                                    animationSpec = tween(
                                        durationMillis = 1000
                                    )
                                )
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .padding(10.dp, 5.dp, 10.dp, 5.dp)
                                        .background(Color.White),
                                    elevation = 10.dp,
                                    shape = RoundedCornerShape(5.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Produit: ${quantiteDraft.produit_nom} (Q: ${quantiteDraft.quantite})",
                                                    style = TextStyle(
                                                        color = Color.Black,
                                                        fontSize = 18.sp,
                                                        textAlign = TextAlign.Center
                                                    ),
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        stockViewModel.deleteQuantiteDraft(
                                                            quantiteDraft
                                                        )
                                                        deletedFruitList.add(quantiteDraft)
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Delete,
                                                        contentDescription = "Deletion"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}