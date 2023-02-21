package be.marche.apptravaux.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import kotlinx.coroutines.flow.Flow

@Dao
interface AvaloirDao {

    @Query("SELECT * FROM avaloir WHERE idReferent = 0")
    fun getAllAvaloirsDraftsList(): List<Avaloir>

    @Query("SELECT * FROM datenettoyage WHERE idReferent = 0")
    fun getAllDatesNettoyagesDraftsList(): List<DateNettoyage>

    @Query("SELECT * FROM commentaire WHERE idReferent = 0")
    fun getAllCommentairesDraftsList(): List<Commentaire>

    @Query("SELECT * FROM avaloir ORDER BY createdAt DESC")
    fun getAll(): List<Avaloir>

    @Query("SELECT * FROM avaloir WHERE idReferent = 0")
    fun getAllDraftsFlow(): Flow<List<Avaloir>>

    @Query("SELECT * FROM avaloir ORDER BY createdAt DESC")
    fun getFlowList(): Flow<List<Avaloir>>

    @Query("SELECT * FROM avaloir WHERE idReferent = :avaloirId")
    fun getFlowById(avaloirId: Int): Flow<Avaloir>

    @Query("SELECT * FROM datenettoyage WHERE avaloirId = :avaloirId ORDER BY createdAt DESC")
    fun getDatesByAvaloirIdFlow(avaloirId: Int): Flow<List<DateNettoyage>>

    @Query("SELECT * FROM commentaire WHERE avaloirId = :avaloirId ORDER BY createdAt DESC")
    fun getCommentairesByAvaloirIdFlow(avaloirId: Int): Flow<List<Commentaire>>

    @Query("SELECT * FROM avaloir WHERE idReferent = :avaloirId")
    fun getById(avaloirId: Int): Avaloir

    @Query("SELECT * FROM avaloir WHERE idReferent = :avaloirId")
    fun getByIdFlow(avaloirId: Int): Flow<Avaloir>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvaloirs(avaloirs: List<Avaloir>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAvaloirsNotSuspend(avaloirs: List<Avaloir>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatesNotSuspend(dates: List<DateNettoyage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCommentairesNotSuspend(commentaires: List<Commentaire>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDates(dates: List<DateNettoyage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentaires(commentaires: List<Commentaire>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAvaloir(avaloir: Avaloir)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentaire(commentaire: Commentaire)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDateNettoyage(dateNettoyage: DateNettoyage)

    @Delete()
    suspend fun deleteAvaloirDraft(avaloirDraft: Avaloir)

    @Delete()
    fun deleteAvaloirDraftNotSuspend(avaloirDraft: Avaloir)

    @Delete()
    fun deleteDateNettoyageNotSuspend(dateNettoyage: DateNettoyage)

    @Delete()
    fun deleteCommentaireNotSuspend(commentaire: Commentaire)

    @RawQuery
    fun findAllAvaloirsByGeoQuery(query: SupportSQLiteQuery): List<Avaloir>

    @Query("SELECT COUNT(*) FROM avaloir")
    fun countAvaloirs(): Int

    @Query("SELECT COUNT(idReferent) FROM commentaire")
    fun countCommentaires(): Int

    @Query("SELECT COUNT(idReferent) FROM dateNettoyage")
    fun countDatesNettoyages(): Int
}
