package be.marche.apptravaux.repository

import be.marche.apptravaux.database.ErrorDao
import be.marche.apptravaux.entities.ErrorLog
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ErrorRepository @Inject constructor(
    private val errorDao: ErrorDao
) {

    fun getAll(): Flow<List<ErrorLog>> {
        return errorDao.getAllErrorsList()
    }

    suspend fun insertErrors(error: ErrorLog) {
        errorDao.insertErrorLog(error)
    }

    suspend fun deleteAll() {
        errorDao.vider()
    }
}