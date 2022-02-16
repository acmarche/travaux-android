package be.marche.apptravaux.entities

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