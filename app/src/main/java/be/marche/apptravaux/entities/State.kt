package be.marche.apptravaux.entities

import be.marche.apptravaux.ui.entities.SearchResponse

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
    class Success(val message: String) : CreateFileState()
    class Error(val message: String) : CreateFileState()
}