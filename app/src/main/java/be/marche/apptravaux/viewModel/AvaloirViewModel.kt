package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.AvaloirUiState
import be.marche.apptravaux.entities.CreateFileState
import be.marche.apptravaux.entities.SearchResponseUiState
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.repository.AvaloirRepository
import be.marche.apptravaux.ui.entities.SearchRequest
import be.marche.apptravaux.utils.FileHelper
import com.google.android.libraries.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
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

    /**
     * Fetch from Api
     */
    private val _uiState = MutableStateFlow<AvaloirUiState>(AvaloirUiState.Empty)
    val uiState: StateFlow<AvaloirUiState> = _uiState

    init {
        fetchAvaloirsFromApi()
    }

    private fun fetchAvaloirsFromApi() {
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

    private val _selectedAvaloir: MutableStateFlow<Avaloir?> = MutableStateFlow(null)
    val selectedAvaloir: StateFlow<Avaloir?> = _selectedAvaloir

    fun getSelectedAvaloir(avaloirId: Int) {
        viewModelScope.launch {
            avaloirRepository.findByIdFlow(avaloirId).collect { task ->
                _selectedAvaloir.value = task
            }
        }
    }

    val allAvaloirs: Flow<List<Avaloir>> = flow {
        // viewModelScope.launch {
        val latestNews = avaloirRepository.getAllAvaloirsFromApi()
        emit(latestNews)
        //  }
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

    /**
     * SEARCHING
     */
    private val _resultSearch = MutableStateFlow<SearchResponseUiState>(SearchResponseUiState.Empty)
    val resultSearch: StateFlow<SearchResponseUiState> = _resultSearch

    fun search(latitude: Double, longitude: Double, distance: String) {

        _resultSearch.value = SearchResponseUiState.Loading

        viewModelScope.launch {
            val response =
                avaloirService.searchAvaloir(SearchRequest(latitude, longitude, distance))
            if (response.isSuccessful) {
                response.body()?.let { searchResponse ->
                    Log.d("ZEZE", "response search $searchResponse")
                    _resultSearch.value = SearchResponseUiState.Loaded(searchResponse)
                }
            } else {
                _resultSearch.value =
                    SearchResponseUiState.Error("Erreur survenue: ${response.message()}")
            }
        }
    }

    /**
     * LOCATION
     */
    private var _locationPermissionGranted = MutableLiveData(true)
    var locationPermissionGranted: LiveData<Boolean> = _locationPermissionGranted

    private var _userCurrentLatLng = mutableStateOf(LatLng(0.0, 0.0))
    var userCurrentLatLng: MutableState<LatLng> = _userCurrentLatLng

    /**
     * CREATE FILE TEMP
     */
    private val _createFile = MutableStateFlow<CreateFileState>(CreateFileState.Empty)
    val resultCreateFile: StateFlow<CreateFileState> = _createFile

    fun createFileForSaving() {
        viewModelScope.launch {
            val fileHelper = FileHelper()
            try {
                _createFile.value =
                    CreateFileState.Success(fileHelper.createImageFile(applicationContext))
            } catch (exception: Exception) {
                _createFile.value =
                    CreateFileState.Error("Erreur lors de le l'enregistrement de l'image: ${exception.message}")
            }
        }
    }
}
