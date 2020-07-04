package be.marche.apptravaux.stock.categorie

import be.marche.apptravaux.stock.database.StockDao
import be.marche.apptravaux.stock.entity.Categorie

class CategorieRepository(private val stockDao: StockDao) {

    fun getAllCategories(): List<Categorie> {
        return stockDao.getAllCategories()
    }

    fun getCagorieById(ecoldeId: Int): Categorie {
        return stockDao.getCagorieById(ecoldeId)
    }

    suspend fun insertCategories(categories: List<Categorie>) {
        stockDao.insertCategories(categories)
    }
}