package be.marche.apptravaux.stock.produit

import androidx.lifecycle.LiveData
import be.marche.apptravaux.stock.database.StockDao
import be.marche.apptravaux.stock.entity.Categorie
import be.marche.apptravaux.stock.entity.Produit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent

class ProduitRepository(private val stockDao: StockDao) : KoinComponent {

    fun getAllProduits(): List<Produit> {
        return stockDao.getAllProduits()
    }

    fun getProduitById(ficheId: Int): LiveData<Produit> {
        return stockDao.getProduitById(ficheId)
    }

    fun getProduitsByCategorie(categorie: Categorie): LiveData<List<Produit>> {
        return stockDao.getProduitsByCategorie(categorie.id)
    }

    suspend fun insertProduits(fiches: List<Produit>) {
        withContext(Dispatchers.IO) {
            stockDao.insertProduits(fiches)
        }
    }

    suspend fun updateProduit(produit: Produit) {
        withContext(Dispatchers.IO) {
            stockDao.updateProduit(produit)
        }
    }
}