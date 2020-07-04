package be.marche.apptravaux.stock.database

import androidx.lifecycle.LiveData
import androidx.room.*
import be.marche.apptravaux.stock.entity.Categorie
import be.marche.apptravaux.stock.entity.Produit

@Dao
interface StockDao {

    /**
     * Categorie
     */
    @Query("SELECT * FROM categorie ORDER BY nom ASC")
    fun getAllCategories(): List<Categorie>

    @Query("SELECT * FROM categorie WHERE id = :categorieId")
    fun getCagorieById(categorieId: Int): Categorie

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Categorie>)

    /**
     * Produit
     */
    @Query("SELECT * FROM produit ORDER BY nom ASC")
    fun getAllProduits(): List<Produit>

    @Query("SELECT * FROM produit ORDER BY nom ASC")
    fun getAllProduitsLive(): LiveData<List<Produit>>

    @Query("SELECT * FROM produit WHERE id = :produitId")
    fun getProduitById(produitId: Int): Produit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduits(produits: List<Produit>)

    @Update
    suspend fun updateProduit(produit: Produit)

    @Query("SELECT * FROM produit WHERE categorie_id = :categorieId ORDER BY nom ASC")
    fun getProduitsByCategorie(categorieId: Int): List<Produit>
}
