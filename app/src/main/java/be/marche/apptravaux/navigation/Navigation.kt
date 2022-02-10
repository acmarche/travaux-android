package be.marche.apptravaux.navigation


import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.marche.apptravaux.AvaloirAddActivity
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
        startDestination = TravauxScreens.PermissionScreen.route
    ) {

        val intent = Intent(navController.context, PermissionActivity::class.java)

        composable(route = TravauxScreens.PermissionScreen.route) {
            PermissionsScreen(
                { navController.navigate(TravauxScreens.HomeScreen.route) },
                { startActivity(navController.context, intent, null) }
            )
        }

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
            val intent = Intent(navController.context, AvaloirAddActivity::class.java)
            startActivity(navController.context, intent, null)
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
            //BottomAppBarWithFabC()
            val intent = Intent(navController.context, PermissionActivity::class.java)
            startActivity(navController.context, intent, null)
        }
    }
}