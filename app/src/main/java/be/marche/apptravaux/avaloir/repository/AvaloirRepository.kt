package be.marche.apptravaux.avaloir.repository

import androidx.lifecycle.LiveData
import be.marche.apptravaux.api.TravauxService
import be.marche.apptravaux.avaloir.database.AvaloirDao
import be.marche.apptravaux.avaloir.entity.Avaloir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent

class AvaloirRepository(
    private val avaloirDao: AvaloirDao,
    val travauxService: TravauxService
) : KoinComponent {

    suspend fun getAllAvaloirsFromApi() = travauxService.getAllAvaloirs()

    fun getAll(): List<Avaloir> {
        return avaloirDao.getAll()
    }

    fun getById(avaloirId: Int): LiveData<Avaloir> {
        return avaloirDao.getById(avaloirId)
    }

    suspend fun insertAvaloirs(avaloirs: List<Avaloir>) {
        avaloirDao.insertAvaloirs(avaloirs)
    }

}