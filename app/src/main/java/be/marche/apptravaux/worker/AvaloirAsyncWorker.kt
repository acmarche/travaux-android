package be.marche.apptravaux.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.SystemClock.sleep
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.ErrorLog
import be.marche.apptravaux.entities.NotificationState
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.repository.AvaloirRepository
import be.marche.apptravaux.repository.ErrorRepository
import be.marche.apptravaux.ui.entities.Coordinates
import be.marche.apptravaux.utils.DownloadHelper
import be.marche.apptravaux.utils.FileHelper
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Response
import java.io.File

@HiltWorker
class AvaloirAsyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val avaloirRepository: AvaloirRepository,
    private val errorRepository: ErrorRepository,
    private val avaloirService: AvaloirService,
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val WORK_RESULT = "work_result"
    }

    private val manager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val outputData = Data.Builder().putString(WORK_RESULT, "Synchronisation démarrée")
    lateinit var avaloirs: List<Avaloir>

    override suspend fun doWork(): Result {

        avaloirs = emptyList()
        outputData.putString(WORK_RESULT, "Zeze").build()
        uploadContent()
        sleep(15)
        avaloirs = avaloirService.fetchAllAvaloirs()
        downloadContent()
        sleep(15)
        downloadImagesContent()

        outputData.putString(WORK_RESULT, "Synchronisation finie").build()
        return Result.success(outputData.build())
    }

    private suspend fun downloadContent() {
        var result = downloadAvaloirs()
        when (result) {
            is NotificationState.Error -> {
                showNotification(
                    "message_channel_downloAvaloirs", 1,
                    "downloAvaloirs",
                    "Avaloirs error: ${result.message}"
                )
            }
            is NotificationState.Success -> {}
        }
        result = downloadDates()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_downlodate", 2,
                "download dates",
                "Dates error: ${result.message}"
            )
            is NotificationState.Success -> {}
        }

        result = downloadCommentaires()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_downloadCommentaires", 3,
                "download Commentaires",
                "Commentaires error: ${result.message}"
            )
            is NotificationState.Success -> {}
        }
    }

    private suspend fun downloadImagesContent() {
        when (val result = downloadImages()) {
            is NotificationState.Error -> showNotification(
                "message_channel_downloadImages", 3,
                "download Images",
                "Images download error: ${result.message}"
            )
            is NotificationState.Success -> {}
        }
    }

    private fun uploadContent() {
        var results = uploadAvaloir()
        treatmentResults("uploadAvaloir", 4, results)

        results = uploadDatesNettoyage()
        treatmentResults("uploadDatesNettoyage", 5, results)

        results = uploadCommentaires()
        treatmentResults("uploadCommentaire", 6, results)
    }

    private suspend fun downloadAvaloirs(): NotificationState {
        try {
            var errorResult = ""
            try {
                avaloirRepository.insertAvaloirs(avaloirs)
            } catch (e: Exception) {
                errorResult = "error insert avaloirs: ${e.message}"
                insertError("download Avaloirs", e.message)
                Firebase.crashlytics.recordException(e)
                NotificationState.Error("${e.message}")
                return NotificationState.Error(errorResult)
            }
            return NotificationState.Success("oki avaloirs")
        } catch (e: Exception) {
            insertError("download Avaloirs fetch", e.message)
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
    }

    private suspend fun downloadDates(): NotificationState {
        try {
            val dates = avaloirService.fetchAllDates()
            val errorResult: String
            try {
                avaloirRepository.insertDates(dates)
            } catch (e: Exception) {
                errorResult = "error comment: ${e.message}"
                Firebase.crashlytics.log("error sync dates ${e.message}")
                insertError("downloadDate message", e.message)
                return NotificationState.Error(errorResult)
            }
            return NotificationState.Success("oki dates")
        } catch (e: Exception) {
            insertError("download Dates", e.message)
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
    }

    private suspend fun downloadCommentaires(): NotificationState {
        try {
            val commentaires = avaloirService.fetchAllCommentaires()
            var errorResult = ""
            try {
                avaloirRepository.insertCommentaires(commentaires)
            } catch (e: Exception) {
                Firebase.crashlytics.log("error sync comment id referent ${e.message}")
                errorResult = "error comment: ${e.message}"
                insertError("download Commentaire message", e.message)
                Firebase.crashlytics.recordException(e)
                return NotificationState.Error(errorResult)
            }
            return NotificationState.Success("oki comments")
        } catch (e: Exception) {
            insertError("fetchAllCommentaires", e.message)
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
    }

    private suspend fun downloadImages(): NotificationState {
        val downloadHelper = DownloadHelper(
            applicationContext,
        )
        try {
            var errorResult = ""
            for (avaloir in avaloirs) {
                if (avaloir.imageUrl?.isEmpty() == true) {
                    continue
                }
                if (avaloir.imageUrl == null) {
                    continue
                }
                try {
                    downloadHelper.downloadImage(
                        avaloir.idReferent,
                        avaloir.imageUrl!!
                    )
                } catch (e: Exception) {
                    errorResult = "error image: ${e.message}"
                    insertError(
                        "downloadImage avaloir id ",
                        "${avaloir.idReferent} :  ${e.message}"
                    )
                    Firebase.crashlytics.recordException(e)
                    return NotificationState.Error(errorResult)
                }
            }
            return NotificationState.Success("oki images")
        } catch (e: Exception) {
            insertError("images fetch", e.message)
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
    }

    private fun uploadAvaloir(): List<NotificationState> {

        val results = mutableListOf<NotificationState>()
        val fileHelper = FileHelper()

        avaloirRepository.getAllAvaloirsDraftsList().forEach { avaloirDraft ->

            val imgFile: File?
            try {
                imgFile = avaloirDraft.imageUrl?.let { File(it) }
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
                results.add(NotificationState.Error("${e.message}"))
                return@forEach
            }

            if (imgFile == null)
                return@forEach

            val requestBody = fileHelper.createRequestBody(imgFile)
            val part = fileHelper.createPart(imgFile, requestBody)
            val coordinates = Coordinates(avaloirDraft.latitude, avaloirDraft.longitude)
            val response = avaloirService.insertAvaloirNotSuspend(coordinates, part, requestBody)

            val res = response.execute()
            if (res is Response && res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val error = dataMessage.error
                    if (error > 0) {
                        results.add(NotificationState.Error(dataMessage.message))
                        Firebase.crashlytics.log("error avaloir upload ${dataMessage.message}")
                        return@forEach
                    }
                    val avaloir = dataMessage.avaloir
                    if (avaloir.idReferent > 0) {
                        try {
                            avaloirRepository.deleteAvaloirDraftNotSuspend(avaloirDraft)
                        } catch (e: Exception) {
                            Firebase.crashlytics.recordException(e)
                            results.add(NotificationState.Error("${e.message}"))
                        }
                    }
                }
            } else {
                Firebase.crashlytics.log("error avaloir upload ${res.code()} ${res.body()}")
                results.add(NotificationState.Error("${res.body()}"))
            }
            results.add(NotificationState.Success("OK"))
        }

        return results
    }

    private fun uploadDatesNettoyage(): List<NotificationState> {

        val results = mutableListOf<NotificationState>()

        avaloirRepository.getAllDatesNettoyagesDraftsList().forEach { dateNettoyage ->

            val date = dateNettoyage.createdAt

            val response = avaloirService.insertDateNotSuspend(dateNettoyage.avaloirId, date)

            val res = response.execute()
            if (res is Response && res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val error = dataMessage.error
                    if (error > 0) {
                        results.add(NotificationState.Error(dataMessage.message))
                        Firebase.crashlytics.log("error upload date ${dataMessage.message}")
                        return@forEach
                    }
                    val dateResult = dataMessage.date
                    if (dateResult.avaloirId > 0) {
                        try {
                            avaloirRepository.deleteDateNettoyageNotSuspend(dateNettoyage)
                        } catch (e: Exception) {
                            Firebase.crashlytics.recordException(e)
                            results.add(NotificationState.Error("${e.message}"))
                            return@forEach
                        }
                    }
                }
            } else {
                Firebase.crashlytics.log("error upload date ${res.code()} ${res.body()}")
                results.add(NotificationState.Error("${res.body()}"))
                return@forEach
            }
        }
        return results.toList()
    }

    private fun uploadCommentaires(): List<NotificationState> {

        val results = mutableListOf<NotificationState>()

        avaloirRepository.getAllCommentairessDraftsList().forEach { commentaire ->

            val response = avaloirService.insertCommentaireNotSuspend(
                commentaire.avaloirId,
                commentaire.content
            )

            val res = response.execute()
            if (res is Response && res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val error = dataMessage.error
                    if (error > 0) {
                        results.add(NotificationState.Error(dataMessage.message))
                        Firebase.crashlytics.log("error upload commentaires ${dataMessage.message}")
                        return@forEach
                    }
                    val commentaireResult = dataMessage.commentaire
                    if (commentaireResult.avaloirId > 0) {
                        try {
                            avaloirRepository.deleteCommentaireNotSuspend(commentaire)
                        } catch (e: Exception) {
                            Firebase.crashlytics.recordException(e)
                            results.add(NotificationState.Error("${e.message}"))
                        }
                    }
                }
            } else {
                Firebase.crashlytics.log("error upload commentaires ${res.code()} ${res.body()}")
                results.add(NotificationState.Error("${res.body()}"))
            }
        }
        return results
    }

    private fun treatmentResults(name: String, idNotify: Int, results: List<NotificationState>) {
        val errors = results.filterIsInstance(NotificationState.Error::class.java)
        if (errors.isNotEmpty()) {
            showNotification(
                "message_channel_$name", idNotify,
                name,
                "$name erreur"
            )
            errors.forEach() {
            }
        }
    }

    private fun showNotification(id: String, idNotify: Int, name: String, desc: String) {

        val channelId = id
        val channelName = name

        val channel =
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        manager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("AppTravaux")
            .setContentText(desc)
            .setSmallIcon(R.drawable.ic_outline_notifications_active_24)

        manager.notify(idNotify, builder.build())
    }

    private suspend fun insertError(sujet: String, message: String?) {
        if (message != null) {
            val error = ErrorLog(null, sujet, message)
            errorRepository.insertErrors(error)
        }
    }
}