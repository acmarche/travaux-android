package be.marche.apptravaux.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.stock.api.StockService
import be.marche.apptravaux.stock.categorie.CategorieRepository
import be.marche.apptravaux.stock.produit.ProduitRepository
import kotlinx.coroutines.launch

class SyncViewModel(
    val stockService: StockService,
    val produitRepository: ProduitRepository,
    val categorieRepository: CategorieRepository

) : ViewModel() {

    fun refreshData() {

        viewModelScope.launch {
            val response = stockService.getAll()
            if (response.isSuccessful) {
                response.body()?.let {
                    categorieRepository.insertCategories(it.categories)
                    produitRepository.insertProduits(it.produits)
                }
            }
        }
    }
}