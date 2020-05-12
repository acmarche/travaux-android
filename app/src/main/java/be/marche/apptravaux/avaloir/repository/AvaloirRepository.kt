package be.marche.apptravaux.avaloir.repository

import be.marche.apptravaux.avaloir.api.AvaloirService
import be.marche.apptravaux.avaloir.database.AvaloirDao
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.DateNettoyage

class AvaloirRepository(
    private val avaloirDao: AvaloirDao,
    private val avaloirService: AvaloirService
) {

    fun getAll(): List<Avaloir> {
        return avaloirDao.getAll()
    }

    fun getDatesByAvaloirId(avaloirId: Int): List<DateNettoyage> {
        return avaloirDao.getDatesByAvaloirId(avaloirId)
    }

    fun getById(avaloirId: Int): Avaloir {
        return avaloirDao.getById(avaloirId)
    }

    suspend fun getAllAvaloirsFromApi() = avaloirService.getAllAvaloirs()
    suspend fun getAllDatesFromApi() = avaloirService.getAllDates()

    suspend fun insertAvaloirs(avaloirs: List<Avaloir>) {
        avaloirDao.insertAvaloirs(avaloirs)
    }

    suspend fun insertDates(dates: List<DateNettoyage>) {
        avaloirDao.insertDates(dates)
    }


}