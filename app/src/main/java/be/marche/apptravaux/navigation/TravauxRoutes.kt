package be.marche.apptravaux.navigation

sealed class TravauxRoutes(val route: String) {
    object HomeScreen : TravauxRoutes("home_screen")
    object SyncScreen : TravauxRoutes("sync_screen")
    object PermissionScreen : TravauxRoutes("permission_screen")
    object PermissionAskScreen : TravauxRoutes("permission_ask_screen")

    /**
     * AVALOIR
     */
    object AvaloirHomeScreen : TravauxRoutes("avaloir_home_screen")
    object AvaloirListScreen : TravauxRoutes("avaloir_list_screen")
    object AvaloirPhotoScreen : TravauxRoutes("avaloir_photo_screen")
    object AvaloirSearchScreen : TravauxRoutes("avaloir_search_screen")
    object AvaloirDetailScreen : TravauxRoutes("avaloir_detail_screen")
    object AvaloirCommentaireScreen : TravauxRoutes("avaloir_commentaire_screen")
    object AvaloirDraftsScreen : TravauxRoutes("avaloir_drafts_screen")

    /**
     * STOCK
     */
    object StockHomeScreen : TravauxRoutes("stock_home_screen")
    object StockListScreen : TravauxRoutes("stock_list_screen")
    object StockDraftScreen : TravauxRoutes("stock_draft_screen")
    object StockAddScreen : TravauxRoutes("stock_add_screen")
    object SettingScreen : TravauxRoutes("setting_screen")
}