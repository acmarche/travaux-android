package be.marche.apptravaux.navigation

sealed class TravauxScreens(val route: String) {
    object HomeScreen : TravauxScreens("home_screen")
    object AvaloirListScreen : TravauxScreens("avaloir_list_screen")
    object DetailScreen : TravauxScreens("detail_screen")
}