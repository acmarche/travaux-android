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
import androidx.work.*
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.*
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.repository.AvaloirRepository
import be.marche.apptravaux.screens.avaloir.AvaloirSyncScreen.Companion.MESSAGE_STATUS
import be.marche.apptravaux.ui.entities.SearchRequest
import be.marche.apptravaux.utils.FileHelper
import be.marche.apptravaux.worker.AvaloirSyncWorker
import com.google.android.libraries.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
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

    private val _allAvaloirsDraftsFlow = MutableStateFlow<List<AvaloirDraft>>(emptyList())
    val allAvaloirsDraftsFlow: StateFlow<List<AvaloirDraft>> = _allAvaloirsDraftsFlow

    init {
        fetchAvaloirsFromDb()
    }

    fun refreshDrafts() {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _allAvaloirsDraftsFlow.value = avaloirRepository.getAllDraftsList()
        }
    }

    private fun fetchAvaloirsFromApi() {
        _uiState.value = AvaloirUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = avaloirRepository.getAllAvaloirsFromApi()
                Timber.d("init viewmodel response api: ${response.toString()}")

                _uiState.value = AvaloirUiState.Loaded(response)

            } catch (ex: Exception) {
                Timber.d("error: ${ex.message}")
                onErrorOccurred()
            }
        }
    }

    private fun fetchAvaloirsFromDb() {
        _uiState.value = AvaloirUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = avaloirRepository.getAll()
                Timber.d("init viewmodel db: ${response.count()}")

                if (response.count() == 0) {
                    _uiState.value = AvaloirUiState.Empty
                } else {
                    _uiState.value = AvaloirUiState.Loaded(response)
                }

            } catch (ex: Exception) {
                Timber.d("error: ${ex.message}")
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
        viewModelScope.launch {
            emit(avaloirRepository.getAllAvaloirsFromApi())
        }
    }

    val allAvaloirsDraftFlow: Flow<List<AvaloirDraft>> = flow {
        avaloirRepository.getAllDraftsFlow()
    }

    /*  fun getAllAvaloirsFromServer(): LiveData<List<Avaloir>> = liveData {
          emit(avaloirRepository.getAllAvaloirsFromApi())
      }*/

    fun insertAvaloir(avaloir: Avaloir) {
        viewModelScope.launch {
            avaloirRepository.insertAvaloirs(listOf(avaloir))
        }
    }

    fun insertAvaloirDraft(avaloir: AvaloirDraft) {
        viewModelScope.launch {
            avaloirRepository.insertAvaloirDraft(avaloir)
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
                    Timber.d("response search $searchResponse")
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
    var currentLatLng: LatLng = LatLng(0.0, 0.0)

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

    /**
     * WorkManager
     */
    val workManager = WorkManager.getInstance(applicationContext)
    val uid: MutableState<UUID> = mutableStateOf(AvaloirSyncWorker.WORK_UUID)

    internal val outputWorkInfos: LiveData<WorkInfo> =
        workManager.getWorkInfoByIdLiveData(uid.value)

    internal fun cancelWork() {
        workManager.cancelUniqueWork("IMAGE_MANIPULATION_WORK_NAME")
    }

    private fun createInputDataForUri(): Data {
        return Data.Builder().putString(MESSAGE_STATUS, "Notification Done.").build()
    }

    fun createRequest(taskData: Data): OneTimeWorkRequest {
        val powerConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val networkConstraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        return OneTimeWorkRequest.Builder(AvaloirSyncWorker::class.java)
            .setConstraints(powerConstraints)
            .setConstraints(networkConstraints)
            .setInputData(taskData)
            .build()
    }

    internal fun enqueueWorkRequest(request: WorkRequest) {
        workManager.enqueue(request)
    }

}
