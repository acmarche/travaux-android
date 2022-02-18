package be.marche.apptravaux.screens.avaloir

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.myricseptember.countryfactcomposefinal.widgets.FloatAlertDialog

@Composable
fun AvaloirSearchScreen(
    navController: NavController,
    avaloirViewModel: AvaloirViewModel= viewModel()
) {
    val content = remember { mutableStateOf("Home Screen") }
    val selectedItem = remember { mutableStateOf("home") }
    val openDialog = remember { mutableStateOf(false) }
    Log.d("TAG", "avaloir search screen")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rechercher"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            content.value = "Navigation Drawer"
                        }
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "")
                    }
                },
                backgroundColor = Colors.Pink500,
                elevation = AppBarDefaults.TopAppBarElevation
            )
        },

        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(15.dp)
            ) {
                Text(
                    text = content.value,
                    color = Color.Black,
                    fontSize = 25.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                FloatAlertDialog(openDialog = openDialog)
            }
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(TravauxRoutes.AvaloirAddScreen.route)
                },
                shape = RoundedCornerShape(50),
                backgroundColor = Colors.Gray900
            ) {
                Icon(Icons.Filled.Add, tint = Color.White, contentDescription = "Add")
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center, //if its not set, it's show default position

        bottomBar = {
            BottomAppBar(
                cutoutShape = RoundedCornerShape(50),
                backgroundColor = Colors.Gray900,
                content = {
                    BottomNavigation {
                        BottomNavigationItem(
                            selected = selectedItem.value == "home",
                            onClick = {
                                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
                            },
                            icon = {
                                Icon(Icons.Filled.Home, contentDescription = "home")
                            },
                            label = { Text(text = "Home") },
                            alwaysShowLabel = false
                        )

                        BottomNavigationItem(
                            selected = selectedItem.value == "Setting",
                            onClick = {
                                content.value = "Setting Screen"
                                selectedItem.value = "setting"
                            },
                            icon = {
                                Icon(Icons.Filled.Search, contentDescription = "search")
                            },
                            label = { Text(text = "Search") },
                            alwaysShowLabel = false
                        )
                    }
                }
            )
        }
    )
}
