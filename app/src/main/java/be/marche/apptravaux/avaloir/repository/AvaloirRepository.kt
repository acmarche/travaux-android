package be.marche.apptravaux.avaloir.repository

import androidx.lifecycle.LiveData
import be.marche.apptravaux.api.TravauxService
import be.marche.apptravaux.avaloir.database.AvaloirDao
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.DateNettoyage
import org.koin.core.KoinComponent

class AvaloirRepository(
    private val avaloirDao: AvaloirDao,
    private val travauxService: TravauxService
) : KoinComponent {

    suspend fun getAllAvaloirsFromApi() = travauxService.getAllAvaloirs()
    suspend fun getAllDatesFromApi() = travauxService.getAllDates()

    fun getAll(): List<Avaloir> {
        return avaloirDao.getAll()
    }

    fun getById(avaloirId: Int): LiveData<Avaloir> {
        return avaloirDao.getById(avaloirId)
    }

    suspend fun insertAvaloirs(avaloirs: List<Avaloir>) {
        avaloirDao.insertAvaloirs(avaloirs)
    }

    suspend fun insertDates(dates: List<DateNettoyage>) {
        avaloirDao.insertDates(dates)
    }

    fun getDatesByAvaloirId(avaloirId: Int): LiveData<List<DateNettoyage>> {
        return avaloirDao.getDatesByAvaloirId(avaloirId)
    }

}