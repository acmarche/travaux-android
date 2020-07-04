package be.marche.apptravaux.stock.categorie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import be.marche.apptravaux.stock.entity.Categorie
import kotlinx.coroutines.Dispatchers

class CategorieViewModel(
    val categorieRepository: CategorieRepository
) : ViewModel(
) {

    init {
        getCategories()
    }

    var categorie: Categorie? = null

    fun getCategories(): LiveData<List<Categorie>> = liveData(Dispatchers.IO) {
        emit(categorieRepository.getAllCategories())
    }

    fun getCagorieById(categorieId: Int): LiveData<Categorie> = liveData {
        emit(categorieRepository.getCagorieById(categorieId))
    }
}