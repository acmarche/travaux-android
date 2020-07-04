package be.marche.apptravaux.stock.produit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.stock.api.StockService
import be.marche.apptravaux.stock.entity.Categorie
import be.marche.apptravaux.stock.entity.Produit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ProduitViewModel(
    val stockService: StockService,
    val produitRepository: ProduitRepository
) : ViewModel() {

    var produits = liveData(Dispatchers.IO) {
        emit(produitRepository.getAllProduits())
    }

    fun getProduitById(produitId: Int): LiveData<Produit> = liveData (Dispatchers.IO){
        emit(produitRepository.getProduitById(produitId))
    }

    fun getProduitsByCategorie(categorie: Categorie): LiveData<List<Produit>> = liveData(Dispatchers.IO) {
        emit(produitRepository.getProduitsByCategorie(categorie))
    }

    fun changeQuantite(produit: Produit, quantite: Int) {
        viewModelScope.launch {
            produit.quantite = quantite
            produitRepository.updateProduit(produit)
     //       produits.value = produitRepository.getAllProduits()
        }
    }

    fun saveAsync(produit: Produit, quantite: Int) {
        viewModelScope.launch {

            val response = stockService.updateProduit(produit.id, quantite)

            if (response.isSuccessful) {
                response.body()?.let {
                    Timber.w("zeze ici " + produit)
                    changeQuantite(produit, quantite)

                }
            }
        }
    }
}