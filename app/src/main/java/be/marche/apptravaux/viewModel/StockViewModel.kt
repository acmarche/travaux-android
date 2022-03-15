package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.CategorieUiState
import be.marche.apptravaux.entities.ProduitUiState
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.networking.StockService
import be.marche.apptravaux.repository.StockRepository
import be.marche.apptravaux.worker.StockWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class StockViewModel @Inject constructor(
    private val stockRepository: StockRepository,
    private val stockService: StockService,
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private val STOCK_SYNC_WORK_REQUEST = "STOCK_SYNC_WORK_REQUEST"

    private val _produitsUiState = MutableStateFlow<ProduitUiState>(ProduitUiState.Empty)
    val produitsUiState: StateFlow<ProduitUiState> = _produitsUiState

    private val _categoriesUiState = MutableStateFlow<CategorieUiState>(CategorieUiState.Empty)
    val categoriessUiState: StateFlow<CategorieUiState> = _categoriesUiState

    fun fetchProduitsFromDb() {
        _produitsUiState.value = ProduitUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = stockRepository.getAllProduits()

                if (response.count() == 0) {
                    _produitsUiState.value = ProduitUiState.Empty
                } else {
                    _produitsUiState.value = ProduitUiState.Loaded(response)
                }

            } catch (ex: Exception) {
                onErrorOccurred()
            }
        }
    }

    fun fetchCategoriesFromDb() {
        _categoriesUiState.value = CategorieUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = stockRepository.getAllCategories()

                if (response.count() == 0) {
                    _categoriesUiState.value = CategorieUiState.Empty
                } else {
                    _categoriesUiState.value = CategorieUiState.Loaded(response)
                }

            } catch (ex: Exception) {
                onErrorOccurred()
            }
        }
    }

    private fun onErrorOccurred() {
        _produitsUiState.value = ProduitUiState.Error(
            applicationContext.getString(R.string.something_went_wrong)
        )
    }

    /**
     * WorkManager
     */
    val workManager by lazy { WorkManager.getInstance(applicationContext) }

    internal fun cancelWork() {
        workManager.cancelUniqueWork(STOCK_SYNC_WORK_REQUEST)
    }

    fun createRequest(taskData: Data): OneTimeWorkRequest {
        val powerConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val networkConstraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        return OneTimeWorkRequest.Builder(StockWorker::class.java)
            .setConstraints(powerConstraints)
            .setConstraints(networkConstraints)
            .setInputData(taskData)
            .addTag("syncStock")
            .build()
    }

    internal fun enqueueWorkRequest(request: OneTimeWorkRequest) {
        workManager.enqueueUniqueWork(
            STOCK_SYNC_WORK_REQUEST,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

}