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
import be.marche.apptravaux.screens.*
import be.marche.apptravaux.screens.avaloir.*
import be.marche.apptravaux.screens.stock.StockHomeScreen
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.viewModel.LocationViewModel

class Navigation {

    companion object {
        const val PARAM_AVALOIR = "avaloirId"
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Navigation(
    avaloirViewModel: AvaloirViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel()
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

        composable(route = TravauxRoutes.SettingScreen.route) {
            val screen = SettingScreen(navController)
            screen.MainScreen()
        }

        /**
         * AVALOIRS
         */
        composable(route = TravauxRoutes.AvaloirHomeScreen.route) {
            val screen = AvaloirHomeScreen(navController)
            screen.HomeScreen()
        }

        composable(route = TravauxRoutes.AvaloirListScreen.route) {
            val screen = AvaloirListScreen(navController)
            screen.ListScreen(avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirDraftsScreen.route) {
            val screen = AvaloirDraftsScreen(navController)
            screen.ListScreen(avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirMapScreen.route) {
            val screen = AvaloirMapScreen(navController)
            screen.ScreenMain(avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirPhotoScreen.route) {
            locationViewModel.stopLocation()
            val screen = AvaloirPhotoScreen(navController)
            screen.TakePicureMain(avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirSearchScreen.route) {
            locationViewModel.start()
            val screen = AvaloirSearchScreen(navController, locationViewModel)
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

        composable(
            route = TravauxRoutes.AvaloirCommentaireScreen.route + "/{$PARAM_AVALOIR}",
            arguments = listOf(navArgument(name = PARAM_AVALOIR) {
                type = NavType.IntType
            })
        ) { entry ->
            val screen = AvaloirCommentaireScreen(
                navController,
                avaloirViewModel
            )
            screen.Main(
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
    }
}