package be.marche.apptravaux.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import kotlinx.coroutines.flow.Flow

@Dao
interface AvaloirDao {

    @Query("SELECT * FROM avaloir")
    fun getAll(): List<Avaloir>

    @Query("SELECT * FROM avaloir")
    fun getFlowList(): Flow<List<Avaloir>>

    @Query("SELECT * FROM avaloir WHERE idReferent = :avaloirId")
    fun getFlowById(avaloirId: Int): Flow<Avaloir>

    @Query("SELECT * FROM datenettoyage WHERE avaloirId = :avaloirId")
    fun getDatesByAvaloirId(avaloirId: Int): List<DateNettoyage>

    @Query("SELECT * FROM commentaire WHERE avaloirId = :avaloirId")
    fun getCommentairesByAvaloirId(avaloirId: Int): List<Commentaire>

    @Query("SELECT * FROM avaloir WHERE idReferent = :avaloirId")
    fun getById(avaloirId: Int): Avaloir

    @Query("SELECT * FROM avaloir WHERE idReferent = :avaloirId")
    fun getByIdFlow(avaloirId: Int):  Flow<Avaloir>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvaloirs(avaloirs: List<Avaloir>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDates(dates: List<DateNettoyage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentaires(commentaires: List<Commentaire>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(avaloir: Avaloir)

}