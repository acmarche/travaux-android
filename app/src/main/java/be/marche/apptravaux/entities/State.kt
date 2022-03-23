package be.marche.apptravaux.entities

import androidx.compose.runtime.Stable
import be.marche.apptravaux.ui.entities.SearchResponse
import java.io.File

sealed class UiState {
    object SignedOut : UiState()
    object InProgress : UiState()
    object Error : UiState()
    object SignIn : UiState()
}

sealed class NotificationState {
    class Success(val message: String) : NotificationState()
    class Error(val message: String) : NotificationState()
}

sealed class AvaloirUiState {
    object Empty : AvaloirUiState()
    object Loading : AvaloirUiState()
    class Loaded(val data: List<Avaloir>) : AvaloirUiState()
    class Error(val message: String) : AvaloirUiState()
}

sealed class ProduitUiState {
    object Empty : ProduitUiState()
    object Loading : ProduitUiState()
    class Loaded(val data: List<Produit>) : ProduitUiState()
    class Error(val message: String) : ProduitUiState()
}

sealed class CategorieUiState {
    object Empty : CategorieUiState()
    object Loading : CategorieUiState()
    class Loaded(val data: List<Categorie>) : CategorieUiState()
    class Error(val message: String) : CategorieUiState()
}

sealed class ResponseUiState {
    object Empty : ResponseUiState()
    object Loading : ResponseUiState()
    class Loaded(val avaloirs: List<Avaloir>) : ResponseUiState()
    class Error(val message: String) : ResponseUiState()
}

sealed class SearchResponseUiState {
    object Empty : SearchResponseUiState()
    object Loading : SearchResponseUiState()
    class Loaded(val response: SearchResponse) : SearchResponseUiState()
    class Error(val message: String) : SearchResponseUiState()
}

sealed class CreateFileState {
    object Empty : CreateFileState()
    class Success(val file: File) : CreateFileState()
    class Error(val message: String) : CreateFileState()
}

sealed class DataState<out R> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()
}


data class StockData(
    val categories: List<Categorie>,
    val produits: List<Produit>
)

@Stable
interface UiState2<T : Result<T>> {
    val value: T?
    val exception: Throwable?

    val hasError: Boolean
        get() = exception != null
}
