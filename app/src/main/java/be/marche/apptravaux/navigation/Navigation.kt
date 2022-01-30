package be.marche.apptravaux.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.marche.apptravaux.screens.DetailScreen
import be.marche.apptravaux.screens.HomeScreen
import be.marche.apptravaux.screens.avaloir.AvaloirListScreen
import be.marche.apptravaux.viewModel.AvaloirViewModel

@Composable
fun Navigation(
    avaloirViewModel: AvaloirViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = TravauxScreens.HomeScreen.route) {
        composable(route = TravauxScreens.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = TravauxScreens.AvaloirListScreen.route) {
            AvaloirListScreen(navController = navController, avaloirViewModel=avaloirViewModel)
        }
        composable(
            route = TravauxScreens.DetailScreen.route + "/{countryId}",
            arguments = listOf(navArgument(name = "countryId") {
                type = NavType.IntType
            })
        ) { entry ->
            DetailScreen(navController, entry.arguments?.getInt("countryId"))
        }
    }
}