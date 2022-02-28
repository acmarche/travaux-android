package be.marche.apptravaux.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.marche.apptravaux.navigation.Navigation.Companion.PARAM_AVALOIR
import be.marche.apptravaux.screens.AvaloirDetailScreen
import be.marche.apptravaux.screens.HomeScreen
import be.marche.apptravaux.screens.PermissionsAskScreen
import be.marche.apptravaux.screens.PermissionsScreen
import be.marche.apptravaux.screens.avaloir.AvaloirAddScreen
import be.marche.apptravaux.screens.avaloir.AvaloirHomeScreen
import be.marche.apptravaux.screens.avaloir.AvaloirListScreen
import be.marche.apptravaux.screens.avaloir.AvaloirSearchScreen
import be.marche.apptravaux.screens.avaloir.AvaloirSyncScreen
import be.marche.apptravaux.screens.stock.StockHomeScreen
import be.marche.apptravaux.viewModel.AvaloirViewModel

class Navigation {

    companion object {
        const val PARAM_AVALOIR = "avaloirId"
    }

}

@OptIn(ExperimentalMaterialApi::class)
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
            AvaloirHomeScreen(navController)
        }

        composable(route = TravauxRoutes.AvaloirListScreen.route) {
            AvaloirListScreen(navController, avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirAddScreen.route) {
            val screen = AvaloirAddScreen(navController)
            screen.AddScreenMain(avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirPhotoScreen.route) {
            val screen = AvaloirAddScreen(navController)
            screen.TakePicureMain(avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirSearchScreen.route) {
            val screen = AvaloirSearchScreen(navController)
            screen.SearchMainScreen(avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirSyncScreen.route) {
            val screen = AvaloirSyncScreen(navController)
            screen.SyncContent(avaloirViewModel)
        }

        composable(
            route = TravauxRoutes.AvaloirDetailScreen.route + "/{$PARAM_AVALOIR}",
            arguments = listOf(navArgument(name = PARAM_AVALOIR) {
                type = NavType.IntType
            })
        ) { entry ->
            val screen = AvaloirDetailScreen(
                navController,
                avaloirViewModel
            )
            screen.AvaloirDetailScreenMain(
                entry.arguments?.getInt(PARAM_AVALOIR)
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