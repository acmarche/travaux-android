package be.marche.apptravaux.navigation

sealed class TravauxScreens(val route: String) {
    object HomeScreen : TravauxScreens("home_screen")
    object PermissionScreen : TravauxScreens("permission_screen")
    object AvaloirHomeScreen : TravauxScreens("avaloir_home_screen")
    object AvaloirListScreen : TravauxScreens("avaloir_list_screen")
    object AvaloirAddScreen : TravauxScreens("avaloir_add_screen")
    object AvaloirSearchScreen : TravauxScreens("avaloir_search_screen")
    object AvaloirDetailScreen : TravauxScreens("avaloir_detail_screen")
    object StockHomeScreen : TravauxScreens("stock_home_screen")
    object StockListScreen : TravauxScreens("stock_home_screen")
    object DemoScreen : TravauxScreens("demo_screen")
}