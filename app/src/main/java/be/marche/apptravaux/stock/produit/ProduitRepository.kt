package be.marche.apptravaux.stock.produit

import androidx.lifecycle.LiveData
import be.marche.apptravaux.stock.database.StockDao
import be.marche.apptravaux.stock.entity.Categorie
import be.marche.apptravaux.stock.entity.Produit

class ProduitRepository(private val stockDao: StockDao) {

    fun getAllProduits(): List<Produit> {
        return stockDao.getAllProduits()
    }

    fun getAllProduitsLive(): LiveData<List<Produit>> {
        return stockDao.getAllProduitsLive()
    }

    fun getProduitById(ficheId: Int): Produit {
        return stockDao.getProduitById(ficheId)
    }

    fun getProduitsByCategorie(categorie: Categorie): List<Produit> {
        return stockDao.getProduitsByCategorie(categorie.id)
    }

    suspend fun insertProduits(produits: List<Produit>) {
        stockDao.insertProduits(produits)
    }

    suspend fun updateProduit(produit: Produit) {
        stockDao.updateProduit(produit)
    }
}