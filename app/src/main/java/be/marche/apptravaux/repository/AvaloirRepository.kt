package be.marche.apptravaux.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import be.marche.apptravaux.database.AvaloirDao
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin

class AvaloirRepository @Inject constructor(
    private val avaloirDao: AvaloirDao
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

    fun findAvaloirByIdFlow(avaloirId: Int): Flow<Avaloir> {
        return avaloirDao.getByIdFlow(avaloirId)
    }

    fun findDatesByIdFlow(avaloirId: Int): Flow<List<DateNettoyage>> {
        return avaloirDao.getDatesByAvaloirIdFlow(avaloirId)
    }

    fun findCommentairesByIdFlow(avaloirId: Int): Flow<List<Commentaire>> {
        return avaloirDao.getCommentairesByAvaloirIdFlow(avaloirId)
    }

    suspend fun insertAvaloirs(avaloirs: List<Avaloir>) {
        avaloirDao.insertAvaloirs(avaloirs)
    }

    suspend fun insertDates(dates: List<DateNettoyage>) {
        avaloirDao.insertDates(dates)
    }

    suspend fun insertCommentaires(commentaires: List<Commentaire>) {
        avaloirDao.insertCommentaires(commentaires)
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

    suspend fun insertCommentaireDb(commentaire: Commentaire) {
        avaloirDao.insertCommentaire(commentaire)
    }

    suspend fun insertDateNettoyageDb(dateNettoyage: DateNettoyage) {
        avaloirDao.insertDateNettoyage(dateNettoyage)
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
        val query = queryString(latitude, longitude, distance)
        val querySql = SimpleSQLiteQuery(query)
        return avaloirDao.findAllAvaloirsByGeoQuery(querySql)
    }

    //https://pixelcarrot.com/listing-nearest-locations-from-sqlite-of-a-mobile-app
    fun queryString(latitude: Double, longitude: Double, radius: Double): String {
        val pi = 3.141592653589793
        val curCosLat = cos(latitude * pi / 180.0)
        val curSinLat = sin(latitude * pi / 180.0)
        val curCosLng = cos(longitude * pi / 180.0)
        val curSinLng = sin(longitude * pi / 180.0)
        val cosRadius = cos(radius / 6371000.0)
        val cosDistance =
            "$curSinLat * sinLatitude + $curCosLat * cosLatitude * (cosLongitude * $curCosLng + sinLongitude * $curSinLng)"

        return """
    SELECT rue,createdAt,idReferent,localite,numero,imageUrl,descriptif,latitude,longitude, $cosDistance AS cos_distance 
    FROM Avaloir
    WHERE $cosDistance > $cosRadius
    ORDER BY $cosDistance DESC
    """
    }

    fun countProduits(): Int {
        return avaloirDao.countAvaloirs()
    }

    fun countCommentaire(): Int {
        return avaloirDao.countCommentaires()
    }

    fun countDateNettoyage(): Int {
        return avaloirDao.countDatesNettoyages()
    }


}