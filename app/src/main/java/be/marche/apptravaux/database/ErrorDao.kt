package be.marche.apptravaux.database

import androidx.room.*
import be.marche.apptravaux.entities.ErrorLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ErrorDao {

    @Query("SELECT * FROM errorlog")
    fun getAllErrorsList(): Flow<List<ErrorLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertErrorLog(errors: ErrorLog)

    @Query("DELETE FROM errorlog")
   suspend fun vider()

}