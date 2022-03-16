package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.*
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

    init {
        fetchCategoriesFromDb()
    }

    fun fetchProduitsFromDb(categorieId: Int) {
        _produitsUiState.value = ProduitUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            val produits: List<Produit>
            try {
                if (categorieId > 0) {
                    produits = stockRepository.getProduitsByCategorie(categorieId)
                } else {
                    produits = stockRepository.getAllProduits()
                }
                if (produits.count() == 0) {
                    _produitsUiState.value = ProduitUiState.Empty
                } else {
                    produits.forEach { produit ->
                        val categorie = getCategorieById(produit.categorie_id)
                        if (categorie != null) {
                            produit.categorieName = categorie.nom
                        }
                    }
                    _produitsUiState.value = ProduitUiState.Loaded(produits)
                }

            } catch (ex: Exception) {
                onErrorOccurred()
            }
        }
    }

    private val _allQuantitesDraftsFlow = MutableStateFlow<List<QuantiteDraft>>(emptyList())
    val allQuantitesDraftsFlow: StateFlow<List<QuantiteDraft>> = _allQuantitesDraftsFlow

    fun refreshDrafts() {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _allQuantitesDraftsFlow.value = stockRepository.getAllQuantitesDraftsList()
        }
    }

    private fun getCategorieById(categorieId: Int): Categorie? {
        return stockRepository.findCategorieById(categorieId)
    }

    var allCategories = emptyList<Categorie>()

    fun fetchCategoriesFromDb() {
        _categoriesUiState.value = CategorieUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = stockRepository.getAllCategories()

                if (response.count() == 0) {
                    _categoriesUiState.value = CategorieUiState.Empty
                } else {
                    _categoriesUiState.value = CategorieUiState.Loaded(response)
                    allCategories = response
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

    fun searchProduit(categorieId: Int) {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _produitsUiState.value = ProduitUiState.Loaded(
                stockRepository.getProduitsByCategorie(categorieId)
            )
        }
    }

    fun updateQuantiteDraft(produitNom: String, produitId: Int, quantite: Int) {
        viewModelScope.launch {
            var quantiteDraft = stockRepository.findQuantiteDraftByIdProduit(produitId)
            if (quantiteDraft == null) {
                quantiteDraft = QuantiteDraft(0, produitNom, produitId, quantite)
            }
            stockRepository.updateQuantiteDraft(quantiteDraft)
        }
    }

    fun upDateQuantite(produit: Produit) {
        viewModelScope.launch {
            stockRepository.updateProduit(produit)
        }
    }

    fun deleteQuantiteDraft(quantiteDraft: QuantiteDraft) {
        viewModelScope.launch {
            stockRepository.deleteQuantiteDraft(quantiteDraft)
        }
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