package be.marche.apptravaux.repository

import be.marche.apptravaux.database.AvaloirDao
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.networking.models.TravauxApiResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AvaloirRepository @Inject constructor (
    private val avaloirDao: AvaloirDao,
    private val avaloirService: AvaloirService
) {

    suspend fun fetchAvaloir(): List<Avaloir> =
        avaloirService.fetchAvaloir()

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

    fun findById(avaloirId: Int): Avaloir {
        return avaloirDao.getById(avaloirId)
    }
    fun findByIdFlow(avaloirId: Int): Flow<Avaloir> {
        return avaloirDao.getByIdFlow(avaloirId)
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