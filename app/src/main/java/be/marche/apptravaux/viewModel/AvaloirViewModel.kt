package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.*
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.repository.AvaloirRepository
import be.marche.apptravaux.ui.entities.SearchRequest
import be.marche.apptravaux.utils.FileHelper
import com.google.android.libraries.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
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


    private var _locationPermissionGranted = MutableLiveData(true)
    var locationPermissionGranted: LiveData<Boolean> = _locationPermissionGranted

    private val _uiState = MutableStateFlow<AvaloirUiState>(AvaloirUiState.Empty)
    val uiState: StateFlow<AvaloirUiState> = _uiState

    var avaloir = MutableStateFlow<Avaloir?>(null)
        private set

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

    fun findByIdT(avaloirId: Int) {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            avaloir.value = avaloirRepository.findById(avaloirId)
        }
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

    private var _userCurrentLatLng = mutableStateOf(LatLng(0.0, 0.0))
    var userCurrentLatLng: MutableState<LatLng> = _userCurrentLatLng

    val allAvaloirs: Flow<List<Avaloir>> = flow {
        // viewModelScope.launch {
        val latestNews = avaloirRepository.getAllAvaloirsFromApi()
        emit(latestNews)
        //  }
    }

    private val _uiState2 = MutableLiveData<UiState>(UiState.SignedOut)
    val uiState2: LiveData<UiState>
        get() = _uiState2


    fun makeFlow(): Flow<Int> = flow {
        repeat(3) { num ->
            delay(1000)
            emit(num)
        }
    }

    suspend fun main() {
        makeFlow()
            .collect { println(it) }
    }

    private val _createFile = MutableStateFlow<CreateFileState>(CreateFileState.Empty)
    val resultCreateFile: StateFlow<CreateFileState> = _createFile

    fun savePhoto(image: Bitmap, context: Context, dir: String) {
        viewModelScope.launch {
            val fileHelper = FileHelper()
            val externalFilesDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            try {
                val file = fileHelper.bitmapToFile(image, externalFilesDir)
                Log.d("ZEZE", " file return $file")
                if (file != null) {
                    Log.d("ZEZE", " file return ${file.path}")
                }
                _createFile.value = CreateFileState.Success("super")
            } catch (exception: Exception) {
                _createFile.value =
                    CreateFileState.Error("Erreur lors de le l'enregistrement de l'image: ${exception.message}")
            }
        }
    }
}
