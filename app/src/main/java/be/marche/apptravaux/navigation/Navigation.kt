package be.marche.apptravaux.navigation


import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.marche.apptravaux.samples.BottomAppBarWithFabC
import be.marche.apptravaux.screens.AvaloirDetailScreen
import be.marche.apptravaux.screens.HomeScreen
import be.marche.apptravaux.screens.avaloir.AvaloirAddScreen
import be.marche.apptravaux.screens.avaloir.AvaloirHomeScreen
import be.marche.apptravaux.screens.avaloir.AvaloirListScreen
import be.marche.apptravaux.screens.avaloir.AvaloirSearchScreen
import be.marche.apptravaux.screens.stock.StockHomeScreen
import be.marche.apptravaux.viewModel.AvaloirViewModel

class Navigation {
    //companion object PARAM_AVALOIR = ""
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Navigation(
    avaloirViewModel: AvaloirViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = TravauxScreens.HomeScreen.route) {

        composable(route = TravauxScreens.HomeScreen.route) {
            HomeScreen(navController = navController)
        }

        /**
         * AVALOIRS
         */
        composable(route = TravauxScreens.AvaloirHomeScreen.route) {
            AvaloirHomeScreen(navController = navController)
        }

        composable(route = TravauxScreens.AvaloirListScreen.route) {
            AvaloirListScreen(navController = navController, avaloirViewModel = avaloirViewModel)
        }

        composable(route = TravauxScreens.AvaloirAddScreen.route) {
            val t =
                AvaloirAddScreen(navController = navController, avaloirViewModel = avaloirViewModel)
            t.TakePicure()
        }

        composable(route = TravauxScreens.AvaloirSearchScreen.route) {
            AvaloirSearchScreen(
                navController = navController,
                avaloirViewModel = avaloirViewModel
            )
        }

        composable(
            route = TravauxScreens.AvaloirDetailScreen.route + "/{avaloirId}",
            arguments = listOf(navArgument(name = "avaloirId") {
                type = NavType.IntType
            })
        ) { entry ->
            AvaloirDetailScreen(
                navController,
                avaloirViewModel,
                entry.arguments?.getInt("avaloirId")
            )
        }

        /**
         * STOCKS
         */
        composable(route = TravauxScreens.StockHomeScreen.route) {
            StockHomeScreen(

            )
        }

        composable(route = TravauxScreens.DemoScreen.route) {
            BottomAppBarWithFabC()
        }
    }
}