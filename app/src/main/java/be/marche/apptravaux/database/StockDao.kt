package be.marche.apptravaux.database

import androidx.room.*
import be.marche.apptravaux.entities.Categorie
import be.marche.apptravaux.entities.Produit
import be.marche.apptravaux.entities.QuantiteDraft
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Query("SELECT * FROM produit ORDER BY nom")
    fun getAllProduits(): List<Produit>

    @Query("SELECT * FROM produit ORDER BY nom")
    fun getAllProduitsFlow(): Flow<List<Produit>>

    @Query("SELECT * FROM categorie ORDER BY nom")
    fun getAllCategories(): List<Categorie>

    @Query("SELECT * FROM categorie ORDER BY nom")
    fun getAllCategoriesFlow(): Flow<List<Categorie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduits(produits: List<Produit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Categorie>)

    @Query("SELECT * FROM categorie WHERE id = :categorieId ")
    fun getCategorieById(categorieId: Int): Categorie?

    @Query("SELECT * FROM quantitedraft WHERE produit_id = :produitId ")
    suspend fun findQuantiteDraftByIdProduit(produitId: Int): QuantiteDraft?

    @Query("SELECT * FROM quantitedraft")
    fun getAllQuantitesDraftsList(): List<QuantiteDraft>

    @Query("SELECT * FROM produit WHERE categorie_id = :categorieId ORDER BY nom ASC")
    fun getProduitsByCategorie(categorieId: Int): List<Produit>

    @Query("SELECT * FROM produit WHERE categorie_id = :categorieId AND nom LIKE '%' || :nom || '%' ORDER BY nom ASC")
    fun getProduitsByCategorieAndName(categorieId: Int?, nom: String?): List<Produit>

    @Query("SELECT * FROM produit WHERE nom LIKE '%' || :nom || '%' ORDER BY nom ASC")
    fun getProduitsByName(nom: String): List<Produit>

    @Query("SELECT COUNT(nom) FROM produit")
    fun countProduits(): Int

    @Update
    suspend fun updateProduit(produit: Produit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateQuantiteDrat(quantiteDraft: QuantiteDraft)

    @Delete()
    suspend fun deleteQuantiteDraft(quantiteDraft: QuantiteDraft)
}