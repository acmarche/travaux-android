package be.marche.apptravaux.navigation

sealed class TravauxRoutes(val route: String) {
    object HomeScreen : TravauxRoutes("home_screen")
    object PermissionScreen : TravauxRoutes("permission_screen")
    object AvaloirHomeScreen : TravauxRoutes("avaloir_home_screen")
    object AvaloirListScreen : TravauxRoutes("avaloir_list_screen")
    object AvaloirAddScreen : TravauxRoutes("avaloir_add_screen")
    object AvaloirSearchScreen : TravauxRoutes("avaloir_search_screen")
    object AvaloirDetailScreen : TravauxRoutes("avaloir_detail_screen")
    object StockHomeScreen : TravauxRoutes("stock_home_screen")
    object StockListScreen : TravauxRoutes("stock_home_screen")
    object DemoScreen : TravauxRoutes("demo_screen")
}