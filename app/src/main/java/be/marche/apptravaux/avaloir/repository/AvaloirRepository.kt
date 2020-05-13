package be.marche.apptravaux.avaloir.repository

import be.marche.apptravaux.avaloir.api.AvaloirService
import be.marche.apptravaux.avaloir.database.AvaloirDao
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.Commentaire
import be.marche.apptravaux.avaloir.entity.DateNettoyage
import kotlinx.coroutines.flow.Flow

class AvaloirRepository(
    private val avaloirDao: AvaloirDao,
    private val avaloirService: AvaloirService
) {

    fun getAll(): List<Avaloir> {
        return avaloirDao.getAll()
    }

    fun getFlow(): Flow<List<Avaloir>> {
        return avaloirDao.getFlow()
    }

    fun getDatesByAvaloirId(avaloirId: Int): List<DateNettoyage> {
        return avaloirDao.getDatesByAvaloirId(avaloirId)
    }

    fun getCommentairesByAvaloirId(avaloirId: Int): List<Commentaire> {
        return avaloirDao.getCommentairesByAvaloirId(avaloirId)
    }

    fun getById(avaloirId: Int): Avaloir {
        return avaloirDao.getById(avaloirId)
    }

    suspend fun getAllAvaloirsFromApi() = avaloirService.getAllAvaloirs()
    suspend fun getAllDatesFromApi() = avaloirService.getAllDates()
    suspend fun getAllCommentairesFromApi() = avaloirService.getAllCommentaires()

    suspend fun insertAvaloirs(avaloirs: List<Avaloir>) {
        avaloirDao.insertAvaloirs(avaloirs)
    }

    suspend fun insertDates(dates: List<DateNettoyage>) {
        avaloirDao.insertDates(dates)
    }

    suspend fun insertCommentaires(commentaires: List<Commentaire>) {
        avaloirDao.insertCommentaires(commentaires)
    }


}