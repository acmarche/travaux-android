package be.marche.apptravaux.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.marche.apptravaux.screens.AvaloirDetailScreen
import be.marche.apptravaux.screens.HomeScreen
import be.marche.apptravaux.screens.PermissionsAskScreen
import be.marche.apptravaux.screens.PermissionsScreen
import be.marche.apptravaux.screens.avaloir.AvaloirAddScreen
import be.marche.apptravaux.screens.avaloir.AvaloirHomeScreen
import be.marche.apptravaux.screens.avaloir.AvaloirListScreen
import be.marche.apptravaux.screens.avaloir.AvaloirSearchScreen
import be.marche.apptravaux.screens.stock.StockHomeScreen
import be.marche.apptravaux.viewModel.AvaloirViewModel

class Navigation {
    //companion object PARAM_AVALOIR = ""
}

@Composable
fun Navigation(
    avaloirViewModel: AvaloirViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TravauxRoutes.PermissionScreen.route
    ) {

        composable(route = TravauxRoutes.PermissionScreen.route) {
            PermissionsScreen(
                { navController.navigate(TravauxRoutes.HomeScreen.route) },
                { navController.navigate(TravauxRoutes.PermissionAskScreen.route) }
            )
        }

        composable(route = TravauxRoutes.PermissionAskScreen.route) {
            PermissionsAskScreen({ navController.navigate(TravauxRoutes.HomeScreen.route) })
        }

        composable(route = TravauxRoutes.HomeScreen.route) {
            HomeScreen(navController)
        }

        /**
         * AVALOIRS
         */
        composable(route = TravauxRoutes.AvaloirHomeScreen.route) {
            AvaloirHomeScreen(navController = navController)
        }

        composable(route = TravauxRoutes.AvaloirListScreen.route) {
            AvaloirListScreen(navController = navController, avaloirViewModel = avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirAddScreen.route) {
            Log.d("ZEZE", "navigation context ${navController.context}")
            val screen = AvaloirAddScreen(avaloirViewModel = avaloirViewModel)
            screen.AddScreenMain(avaloirViewModel, navController)
        }

        composable(route = TravauxRoutes.AvaloirSearchScreen.route) {
            val screen = AvaloirSearchScreen(navController, avaloirViewModel)
            screen.SearchMainScreen()
        }

        composable(
            route = TravauxRoutes.AvaloirDetailScreen.route + "/{avaloirId}",
            arguments = listOf(navArgument(name = "avaloirId") {
                type = NavType.IntType
            })
        ) { entry ->
            Log.d("ZEZE", "navigation ${entry.arguments?.getInt("avaloirId")}")
            AvaloirDetailScreen(
                navController,
                avaloirViewModel,
                entry.arguments?.getInt("avaloirId")
            )
        }

        /**
         * STOCKS
         */
        composable(route = TravauxRoutes.StockHomeScreen.route) {
            StockHomeScreen(
            )
        }

        composable(route = TravauxRoutes.DemoScreen.route) {
            //BottomAppBarWithFabC()
        }
    }
}