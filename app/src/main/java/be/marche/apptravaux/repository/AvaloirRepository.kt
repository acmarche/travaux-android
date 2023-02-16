package be.marche.apptravaux.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import be.marche.apptravaux.database.AvaloirDao
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import be.marche.apptravaux.networking.AvaloirService
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin

class AvaloirRepository @Inject constructor(
    private val avaloirDao: AvaloirDao,
    private val avaloirService: AvaloirService
) {

    fun getAll(): List<Avaloir> {
        return avaloirDao.getAll()
    }

    fun getAllAvaloirsDraftsList(): List<Avaloir> {
        return avaloirDao.getAllAvaloirsDraftsList()
    }

    fun getAllDatesNettoyagesDraftsList(): List<DateNettoyage> {
        return avaloirDao.getAllDatesNettoyagesDraftsList()
    }

    fun getAllCommentairessDraftsList(): List<Commentaire> {
        return avaloirDao.getAllCommentairesDraftsList()
    }

    fun getAllAvaloirsDraftsFlow(): Flow<List<Avaloir>> {
        return avaloirDao.getFlowList()
    }

    fun findAvaloirById(avaloirId: Int): Avaloir {
        return avaloirDao.getById(avaloirId)
    }

    fun findAvaloirByIdFlow(avaloirId: Int): Flow<Avaloir> {
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

    suspend fun insertCommentaires(commentaires: List<Commentaire>) {
        avaloirDao.insertCommentaires(commentaires)
    }

    suspend fun insertDates(dates: List<DateNettoyage>) {
        avaloirDao.insertDates(dates)
    }

    suspend fun insertCommentaireDb(commentaire: Commentaire) {
        avaloirDao.insertCommentaire(commentaire)
    }

    suspend fun insertDateNettoyageDb(dateNettoyage: DateNettoyage) {
        avaloirDao.insertDateNettoyage(dateNettoyage)
    }

    suspend fun insertAvaloir(avaloir: Avaloir) {
        avaloirDao.insertAvaloir(avaloir)
    }

    suspend fun deleteAvaloirDraft(avaloirDraft: Avaloir) {
        avaloirDao.deleteAvaloirDraft(avaloirDraft)
    }

    fun deleteAvaloirDraftNotSuspend(avaloirDraft: Avaloir) {
        avaloirDao.deleteAvaloirDraftNotSuspend(avaloirDraft)
    }

    fun deleteDateNettoyageNotSuspend(dateNettoyage: DateNettoyage) {
        avaloirDao.deleteDateNettoyageNotSuspend(dateNettoyage)
    }

    fun deleteCommentaireNotSuspend(commentaire: Commentaire) {
        avaloirDao.deleteCommentaireNotSuspend(commentaire)
    }

    fun findAvaloirsByGeo(latitude: Double, longitude: Double, distance: Double): List<Avaloir> {

        val query = createQuery(latitude, longitude, distance)

        return avaloirDao.findAllAvaloirsByGeoQuery(query)
    }

    fun createQuery(latitude: Double, longitude: Double, radius: Double): SimpleSQLiteQuery {
        val pi = 3.141592653589793
        val curCosLat = cos(latitude * pi / 180.0)
        val curSinLat = sin(latitude * pi / 180.0)
        val curCosLng = cos(longitude * pi / 180.0)
        val curSinLng = sin(longitude * pi / 180.0)
        val cosRadius = cos(radius / 6371000.0)
        val cosDistance =
            "$curSinLat * sinLatitude + $curCosLat * cosLatitude * (cosLongitude * $curCosLng + sinLongitude * $curSinLng)"

        val queryString =
            "SELECT rue,createdAt,localite,numero,imageUrl,descriptif, latitude, longitude, $cosDistance AS cos_distance FROM Avaloir " +
                    "WHERE $cosDistance > $cosRadius"

        Timber.e("zeze queryString " + queryString)

        return SimpleSQLiteQuery(queryString)
    }
}