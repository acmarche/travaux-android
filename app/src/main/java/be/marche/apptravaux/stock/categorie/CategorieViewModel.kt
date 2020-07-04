package be.marche.apptravaux.stock.categorie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.marche.apptravaux.stock.api.StockService
import be.marche.apptravaux.stock.entity.Categorie

class CategorieViewModel(
    val stockService: StockService,
    val categorieRepository: CategorieRepository
) : ViewModel(
) {

    init {
        loadCategories()
    }

    private lateinit var categories: LiveData<List<Categorie>>
    var categorie: Categorie? = null

    private fun loadCategories() {
        categories = categorieRepository.getAllCategories()
    }

    fun getCategories(): LiveData<List<Categorie>> = categories

    fun getCagorieById(categorieId: Int): LiveData<Categorie> {
        return categorieRepository.getCagorieById(categorieId)
    }
}