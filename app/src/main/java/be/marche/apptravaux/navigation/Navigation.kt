package be.marche.apptravaux.navigation


import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.marche.apptravaux.AvaloirAddActivity
import be.marche.apptravaux.ListActivity
import be.marche.apptravaux.screens.AvaloirDetailScreen
import be.marche.apptravaux.screens.HomeScreen
import be.marche.apptravaux.screens.PermissionActivity
import be.marche.apptravaux.screens.PermissionsScreen
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
    avaloirViewModel: AvaloirViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TravauxRoutes.PermissionScreen.route
    ) {

        val intent = Intent(navController.context, PermissionActivity::class.java)

        composable(route = TravauxRoutes.PermissionScreen.route) {
            PermissionsScreen(
                { navController.navigate(TravauxRoutes.HomeScreen.route) },
                { startActivity(navController.context, intent, null) }
            )
        }

        composable(route = TravauxRoutes.HomeScreen.route) {
            HomeScreen(navController = navController)
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
            Log.d("ZEZE", "nav context ${navController.context}")
            val intent = Intent(navController.context, AvaloirAddActivity::class.java)
            startActivity(navController.context, intent, null)
        }

        composable(route = TravauxRoutes.AvaloirSearchScreen.route) {
            AvaloirSearchScreen(
                navController = navController,
                avaloirViewModel = avaloirViewModel
            )
        }

        composable(
            route = TravauxRoutes.AvaloirDetailScreen.route + "/{avaloirId}",
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
        composable(route = TravauxRoutes.StockHomeScreen.route) {
            StockHomeScreen(

            )
        }

        composable(route = TravauxRoutes.DemoScreen.route) {
            //BottomAppBarWithFabC()

        }
    }
}