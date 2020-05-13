package be.marche.apptravaux.avaloir.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.Commentaire
import be.marche.apptravaux.avaloir.entity.DateNettoyage
import kotlinx.coroutines.flow.Flow

@Dao
interface AvaloirDao {

    @Query("SELECT * FROM avaloir")
    fun getAll(): List<Avaloir>

    @Query("SELECT * FROM avaloir")
    fun getFlow(): Flow<List<Avaloir>>

    @Query("SELECT * FROM datenettoyage WHERE avaloirId = :avaloirId")
    fun getDatesByAvaloirId(avaloirId: Int): List<DateNettoyage>

    @Query("SELECT * FROM commentaire WHERE avaloirId = :avaloirId")
    fun getCommentairesByAvaloirId(avaloirId: Int): List<Commentaire>

    @Query("SELECT * FROM avaloir WHERE id = :avaloirId")
    fun getById(avaloirId: Int): Avaloir

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvaloirs(avaloirs: List<Avaloir>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDates(dates: List<DateNettoyage>)
}