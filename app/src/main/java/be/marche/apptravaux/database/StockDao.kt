package be.marche.apptravaux.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.marche.apptravaux.entities.Categorie
import be.marche.apptravaux.entities.Produit
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Query("SELECT * FROM produit")
    fun getAllProduits(): List<Produit>

    @Query("SELECT * FROM produit")
    fun getAllProduitsFlow(): Flow<List<Produit>>

    @Query("SELECT * FROM categorie")
    fun getAllCategories(): List<Categorie>

    @Query("SELECT * FROM categorie")
    fun getAllCategoriesFlow(): Flow<List<Categorie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduits(produits: List<Produit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Categorie>)


}