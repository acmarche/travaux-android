package be.marche.apptravaux.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.SystemClock.sleep
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import be.marche.apptravaux.R
import be.marche.apptravaux.entities.NotificationState
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.repository.AvaloirRepository
import be.marche.apptravaux.ui.entities.Coordinates
import be.marche.apptravaux.utils.DateUtils
import be.marche.apptravaux.utils.FileHelper
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Response
import java.io.File
import java.util.*

@HiltWorker
class AvaloirSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val avaloirRepository: AvaloirRepository,
    private val avaloirService: AvaloirService,
) : Worker(context, workerParameters) {

    companion object {
        const val WORK_RESULT = "work_result"
    }

    private val manager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val outputData = Data.Builder().putString(WORK_RESULT, "Synchronisation démarrée")

    override fun doWork(): Result {

        outputData.putString(WORK_RESULT, "Zeze").build()

        uploadContent()
        sleep(15)
        downloadContent()

        outputData.putString(WORK_RESULT, "Synchronisation finie").build()
        return Result.success(outputData.build())
    }

    private fun downloadContent() {

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
                "downlodates",
                "Dates error: ${result.message}"
            )
            is NotificationState.Success -> {}
        }

        result = downloadCommentaires()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_uploadCommentaires", 3,
                "uploadCommentaires",
                "Commentaires error: ${result.message}"
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

    private fun downloadAvaloirs(): NotificationState {
        try {
            val response = avaloirService.fetchAllAvaloirsNotSuspend()
            val res = response.execute()
            if (res is Response && res.isSuccessful) {
                res.body()?.let {
                    try {
                        avaloirRepository.insertAvaloirsNotSuspend(it)
                        return NotificationState.Success("OK")
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        return NotificationState.Error("${e.message}")
                    }
                }
            } else {
                Firebase.crashlytics.log("error download avaloirs ${res.code()} ${res.body()}")
                return NotificationState.Error("${res.body()}")
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
        Firebase.crashlytics.log("error download comments empty")
        return NotificationState.Error("Empty")
    }

    private fun downloadDates(): NotificationState {
        try {
            val response = avaloirService.fetchAllDatesNotSuspend()
            val res = response.execute()
            if (res is Response && res.isSuccessful) {
                res.body()?.let {
                    try {
                        avaloirRepository.insertDatesNotSuspend(it)
                        return NotificationState.Success("OK")
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        return NotificationState.Error("${e.message}")
                    }
                }
            } else {
                Firebase.crashlytics.log("error download dates ${res.code()} ${res.body()}")
                return NotificationState.Error("${res.body()}")
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
        Firebase.crashlytics.log("error download comments empty")
        return NotificationState.Error("Empty")
    }

    private fun downloadCommentaires(): NotificationState {
        try {
            val response = avaloirService.fetchAllCommentairesNotSuspend()
            val res = response.execute()
            if (res is Response && res.isSuccessful) {
                res.body()?.let {
                    try {
                        avaloirRepository.insertCommentairesNotSuspend(it)
                        return NotificationState.Success("OK")
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        return NotificationState.Error("${e.message}")
                    }
                }
            } else {
                Firebase.crashlytics.log("error download comments ${res.code()} ${res.body()}")
                return NotificationState.Error("${res.body()}")
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
        Firebase.crashlytics.log("error download comments empty")
        return NotificationState.Error("Empty")
    }

    private fun uploadAvaloir(): List<NotificationState> {

        val results = mutableListOf<NotificationState>()
        val fileHelper = FileHelper()

        avaloirRepository.getAllAvaloirsDraftsList().forEach { avaloirDraft ->

            val imgFile = File(avaloirDraft.imageUrl)
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
                            results.add(NotificationState.Success("OK"))
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
                            results.add(NotificationState.Success("OK"))
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
                            results.add(NotificationState.Success("OK"))
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
        if (errors.count() > 0) {
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
}