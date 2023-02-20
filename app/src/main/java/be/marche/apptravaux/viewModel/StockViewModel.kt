package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.*
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.networking.StockService
import be.marche.apptravaux.repository.StockRepository
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

    private val _produitsUiState = MutableStateFlow<ProduitUiState>(ProduitUiState.Empty)
    val produitsUiState: StateFlow<ProduitUiState> = _produitsUiState

    private val _categoriesUiState = MutableStateFlow<CategorieUiState>(CategorieUiState.Empty)
    val categoriessUiState: StateFlow<CategorieUiState> = _categoriesUiState
    var _countProduit = 0

    init {
        fetchCategoriesFromDb()
    }

    fun fetchProduitsFromDb(categorieId: Int, textSearched: String?) {
        _produitsUiState.value = ProduitUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            var produits: List<Produit> = emptyList()
            try {
                when {
                    categorieId > 0 && textSearched != null -> {
                        produits =
                            stockRepository.getProduitsByCategorieAndName(categorieId, textSearched)
                    }
                    categorieId > 0 && textSearched == null -> {
                        produits = stockRepository.getProduitsByCategorie(categorieId)
                    }
                    categorieId == 0 && textSearched != null -> {
                        produits = stockRepository.getProduitsByName(textSearched)
                    }
                    else -> {
                        produits = stockRepository.getAllProduits()
                        allProduits = produits
                    }
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
    var allProduits = emptyList<Produit>()

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

    fun countProduit() {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _countProduit = stockRepository.countProduit()
        }
    }

    fun updateQuantiteDraft(produitNom: String, produitId: Int, quantite: Int) {
        viewModelScope.launch {
            var quantiteDraft = stockRepository.findQuantiteDraftByIdProduit(produitId)
            if (quantiteDraft == null) {
                quantiteDraft = QuantiteDraft(0, produitNom, produitId, quantite)
            } else
                quantiteDraft.quantite = quantite
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

}