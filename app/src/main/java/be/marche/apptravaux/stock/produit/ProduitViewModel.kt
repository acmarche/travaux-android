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

class ProduitViewModel(
    val stockService: StockService,
    val produitRepository: ProduitRepository
) : ViewModel() {

    var produits = produitRepository.getAllProduitsLive()

    fun getProduitById(produitId: Int): LiveData<Produit> = liveData(Dispatchers.IO) {
        emit(produitRepository.getProduitById(produitId))
    }

    fun getProduitsByCategorie(categorie: Categorie): LiveData<List<Produit>> =
        liveData(Dispatchers.IO) {
            emit(produitRepository.getProduitsByCategorie(categorie))
        }

    fun insertProdui(produit: Produit) {
        viewModelScope.launch {
            produitRepository.insertProduits(listOf(produit))
        }
    }

    fun changeQuantite(produit: Produit, quantite: Int) {
        viewModelScope.launch {
            produit.quantite = quantite
            produitRepository.updateProduit(produit)
          //  saveAsync(produit, produit.quantite)
        }
    }

    fun saveAsync(produit: Produit, quantite: Int) {
        viewModelScope.launch {

            val response = stockService.updateProduit(produit.id, quantite)

            if (response.isSuccessful) {
                response.body()?.let {
        //            changeQuantite(produit, quantite)
                }
            }
        }
    }
}