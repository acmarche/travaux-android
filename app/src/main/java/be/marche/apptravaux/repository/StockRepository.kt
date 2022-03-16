package be.marche.apptravaux.repository

import be.marche.apptravaux.database.StockDao
import be.marche.apptravaux.entities.Categorie
import be.marche.apptravaux.entities.Produit
import be.marche.apptravaux.entities.QuantiteDraft
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.networking.StockService
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockDao: StockDao,
    private val stockService: StockService
) {

    fun getAllProduits(): List<Produit> {
        return stockDao.getAllProduits()
    }

    suspend fun getAllAvaloirsFromApi() = stockService.getAllProduits()

    suspend fun insertProduits(produits: List<Produit>) {
        stockDao.insertProduits(produits)
    }

    suspend fun insertCategories(categories: List<Categorie>) {
        stockDao.insertCategories(categories)
    }

    fun getAllCategories(): List<Categorie> {
        return stockDao.getAllCategories()
    }

    fun getProduitsByCategorie(categorieId: Int): List<Produit> {
        return stockDao.getProduitsByCategorie(categorieId)
    }

    fun findCategorieById(categorieId: Int): Categorie? {
        return stockDao.getCategorieById(categorieId)
    }

    fun getAllQuantitesDraftsList(): List<QuantiteDraft> {
        return stockDao.getAllQuantitesDraftsList()
    }

    suspend fun findQuantiteDraftByIdProduit(produitId: Int): QuantiteDraft? {
        return stockDao.findQuantiteDraftByIdProduit(produitId)
    }

    suspend fun updateProduit(produit: Produit) {
        stockDao.updateProduit(produit)
    }

    suspend fun updateQuantiteDraft(quantiteDraft: QuantiteDraft) {
        stockDao.updateQuantiteDrat(quantiteDraft)
    }

    suspend fun deleteQuantiteDraft(quantiteDraft: QuantiteDraft) {
        stockDao.deleteQuantiteDraft(quantiteDraft)
    }
}