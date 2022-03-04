package be.marche.apptravaux.entities

import be.marche.apptravaux.ui.entities.SearchResponse
import java.io.File

sealed class UiState {
    object SignedOut : UiState()
    object InProgress : UiState()
    object Error : UiState()
    object SignIn : UiState()
}

sealed class AvaloirUiState {
    object Empty : AvaloirUiState()
    object Loading : AvaloirUiState()
    class Loaded(val data: List<Avaloir>) : AvaloirUiState()
    class Error(val message: String) : AvaloirUiState()
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