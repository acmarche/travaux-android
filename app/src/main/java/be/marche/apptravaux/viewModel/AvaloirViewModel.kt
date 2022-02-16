package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.repository.AvaloirRepository
import be.marche.apptravaux.ui.theme.ResponseUiState
import be.marche.apptravaux.ui.theme.SearchRequest
import be.marche.apptravaux.ui.theme.SearchResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class AvaloirViewModel @Inject constructor(
    private val avaloirRepository: AvaloirRepository,
    private val avaloirService: AvaloirService,
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<AvaloirUiState>(AvaloirUiState.Empty)
    val uiState: StateFlow<AvaloirUiState> = _uiState
    var avaloir = MutableStateFlow<Avaloir?>(null)

    init {
        fetchAvaloirs()
    }

    private fun fetchAvaloirs() {
        _uiState.value = AvaloirUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = avaloirRepository.fetchAvaloir();
                Log.d("ZEZE", "init viewmodel response api: ${response.toString()}")

                _uiState.value = AvaloirUiState.Loaded(response)

            } catch (ex: Exception) {
                Log.d("ZEZE", "error: ${ex.message}")
                onErrorOccurred()
            }
        }
    }

    fun findById(avaloirId: Int): Flow<Avaloir> {
        val t = avaloirRepository.findByIdFlow(avaloirId)
        return t.distinctUntilChanged()
    }

    /*   fun findByIdAsLive(avaloirId: Int): LiveData<Avaloir> = liveData {
           avaloir.value = avaloirRepository.findById(avaloirId)
           emit(avaloirRepository.findById(avaloirId))
       }*/

    private fun onQueryLimitReached() {
        _uiState.value = AvaloirUiState.Error(
            applicationContext.getString(R.string.query_limit_reached)
        )
    }

    private fun onErrorOccurred() {
        _uiState.value = AvaloirUiState.Error(
            applicationContext.getString(R.string.something_went_wrong)
        )
    }

    sealed class AvaloirUiState {
        object Empty : AvaloirUiState()
        object Loading : AvaloirUiState()
        class Loaded(val data: List<Avaloir>) : AvaloirUiState()
        class Error(val message: String) : AvaloirUiState()
    }

    /*  fun getAllAvaloirsFromServer(): LiveData<List<Avaloir>> = liveData {
          emit(avaloirRepository.getAllAvaloirsFromApi())
      }*/

    fun insertAvaloir(avaloir: Avaloir) {
        viewModelScope.launch {
            avaloirRepository.insertAvaloirs(listOf(avaloir))
        }
    }

    fun insertAvaloirs(avaloirs: List<Avaloir>) {
        viewModelScope.launch {
            avaloirRepository.insertAvaloirs(avaloirs)
        }
    }

    var resultSearch = MutableStateFlow<ResponseUiState>(ResponseUiState.Empty)

    fun search(latitude: Double, longitude: Double, distance: String) {
        viewModelScope.launch {
            val response =
                avaloirService.searchAvaloir(SearchRequest(latitude, longitude, distance))
            if (response.isSuccessful) {
                response.body()?.let { searchResponse ->
                    resultSearch.value = searchResponse
                }
            }
        }
    }

    sealed class UiState {
        object SignedOut : UiState()
        object InProgress : UiState()
        object Error : UiState()
        object SignIn : UiState()
    }

    private val _uiState2 = MutableLiveData<UiState>(UiState.SignedOut)
    val uiState2: LiveData<UiState>
        get() = _uiState2


}
