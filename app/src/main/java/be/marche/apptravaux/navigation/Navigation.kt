package be.marche.apptravaux.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.marche.apptravaux.location.GeolocationServiceViewModel
import be.marche.apptravaux.navigation.Navigation.Companion.PARAM_AVALOIR
import be.marche.apptravaux.screens.*
import be.marche.apptravaux.screens.avaloir.*
import be.marche.apptravaux.screens.stock.StockAddScreen
import be.marche.apptravaux.screens.stock.StockDraftScreen
import be.marche.apptravaux.screens.stock.StockHomeScreen
import be.marche.apptravaux.screens.stock.StockListScreen
import be.marche.apptravaux.viewModel.*

class Navigation {

    companion object {
        const val PARAM_AVALOIR = "avaloirId"
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Navigation(
    avaloirViewModel: AvaloirViewModel = viewModel(),
    locationViewModel: GeolocationServiceViewModel = viewModel(),
    stockViewModel: StockViewModel = viewModel(),
    workerViewModel: WorkerViewModel = viewModel(),
    errorViewModel: ErrorViewModel = viewModel()
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

        composable(route = TravauxRoutes.SyncScreen.route) {
            val screen = SyncScreen(navController)
            screen.MaintContent(workerViewModel, avaloirViewModel, stockViewModel)
        }

        composable(route = TravauxRoutes.SettingScreen.route) {
            val screen = SettingScreen(navController)
            screen.MainScreen(errorViewModel)
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


        composable(route = TravauxRoutes.AvaloirPhotoScreen.route) {
            val screen = AvaloirPhotoScreen(navController)
            screen.TakePicureMain(avaloirViewModel)
        }

        composable(route = TravauxRoutes.AvaloirSearchScreen.route) {
            val screen = AvaloirSearchScreen(navController, locationViewModel)
            screen.SearchMainScreen(avaloirViewModel)
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
            val screen = StockHomeScreen(navController)
            screen.HomeScreen()
        }

        composable(route = TravauxRoutes.StockListScreen.route) {
            val screen = StockListScreen(navController, stockViewModel)
            screen.ListScreen()
        }

        composable(route = TravauxRoutes.StockAddScreen.route) {
            val screen = StockAddScreen(navController)
            screen.Main(stockViewModel)
        }

        composable(route = TravauxRoutes.StockDraftScreen.route) {
            val screen = StockDraftScreen(navController)
            screen.MainScreen(stockViewModel)
        }
    }
}