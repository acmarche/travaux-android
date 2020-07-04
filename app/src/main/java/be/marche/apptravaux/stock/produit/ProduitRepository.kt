package be.marche.apptravaux.stock.produit

import be.marche.apptravaux.stock.database.StockDao
import be.marche.apptravaux.stock.entity.Categorie
import be.marche.apptravaux.stock.entity.Produit

class ProduitRepository(private val stockDao: StockDao) {

    fun getAllProduits(): List<Produit> {
        return stockDao.getAllProduits()
    }

    fun getProduitById(ficheId: Int): Produit {
        return stockDao.getProduitById(ficheId)
    }

    fun getProduitsByCategorie(categorie: Categorie): List<Produit> {
        return stockDao.getProduitsByCategorie(categorie.id)
    }

    suspend fun insertProduits(fiches: List<Produit>) {
        stockDao.insertProduits(fiches)
    }

    suspend fun updateProduit(produit: Produit) {
        stockDao.updateProduit(produit)
    }
}