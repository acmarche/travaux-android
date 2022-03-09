package be.marche.apptravaux.repository

import be.marche.apptravaux.database.AvaloirDao
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.AvaloirDraft
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import be.marche.apptravaux.networking.AvaloirService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AvaloirRepository @Inject constructor(
    private val avaloirDao: AvaloirDao,
    private val avaloirService: AvaloirService
) {

    fun getAll(): List<Avaloir> {
        return avaloirDao.getAll()
    }

    fun getAllDraftsList(): List<AvaloirDraft> {
        return avaloirDao.getAllDraftsList()
    }

    fun getAllDraftsFlow(): Flow<List<Avaloir>> {
        return avaloirDao.getFlowList()
    }

    fun findById(avaloirId: Int): Avaloir {
        return avaloirDao.getById(avaloirId)
    }

    fun findByIdFlow(avaloirId: Int): Flow<Avaloir> {
        return avaloirDao.getByIdFlow(avaloirId)
    }

    fun findDatesByIdFlow(avaloirId: Int): Flow<List<DateNettoyage>> {
        return avaloirDao.getDatesByAvaloirIdFlow(avaloirId)
    }

    fun findCommentairesByIdFlow(avaloirId: Int): Flow<List<Commentaire>> {
        return avaloirDao.getCommentairesByAvaloirIdFlow(avaloirId)
    }

    suspend fun getAllAvaloirsFromApi() = avaloirService.fetchAllAvaloirs()

    suspend fun insertAvaloirs(avaloirs: List<Avaloir>) {
        avaloirDao.insertAvaloirs(avaloirs)
    }

    fun insertAvaloirsNotSuspend(avaloirs: List<Avaloir>) {
        avaloirDao.insertAvaloirsNotSuspend(avaloirs)
    }

    fun insertDatesNotSuspend(dates: List<DateNettoyage>) {
        avaloirDao.insertDatesNotSuspend(dates)
    }

    fun insertCommentairesNotSuspend(commentaires: List<Commentaire>) {
        avaloirDao.insertCommentairesNotSuspend(commentaires)
    }

    suspend fun insertDates(dates: List<DateNettoyage>) {
        avaloirDao.insertDates(dates)
    }

    suspend fun insertCommentaireDb( commentaire: Commentaire) {
        avaloirDao.insertCommentaire(commentaire)
    }

    suspend fun insertDateNettoyageDb(dateNettoyage: DateNettoyage) {
        avaloirDao.insertDateNettoyage(dateNettoyage)
    }

    suspend fun insertAvaloirDraft(avaloir: AvaloirDraft) {
        avaloirDao.insertAvaloirDraft(avaloir)
    }

    suspend fun deleteAvaloirDraft(avaloirDraft: AvaloirDraft) {
        avaloirDao.deleteAvaloirDraft(avaloirDraft)
    }

    fun deleteAvaloirDraftNotSuspend(avaloirDraft: AvaloirDraft) {
        avaloirDao.deleteAvaloirDraftNotSuspend(avaloirDraft)
    }

}