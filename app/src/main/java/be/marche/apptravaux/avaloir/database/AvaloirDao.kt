package be.marche.apptravaux.avaloir.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.marche.apptravaux.avaloir.entity.Avaloir

@Dao
interface AvaloirDao {

    @Query("SELECT * FROM avaloir")
    fun getAll(): List<Avaloir>

    @Query("SELECT * FROM avaloir WHERE id = :avaloirId")
    fun getById(avaloirId: Int): LiveData<Avaloir>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvaloirs(avaloirs: List<Avaloir>)

}