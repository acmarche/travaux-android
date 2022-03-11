package be.marche.apptravaux.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
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
import be.marche.apptravaux.screens.avaloir.AvaloirSyncScreen
import be.marche.apptravaux.ui.entities.Coordinates
import be.marche.apptravaux.utils.FileHelper
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.UUID.randomUUID

@HiltWorker
class AvaloirSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val avaloirRepository: AvaloirRepository,
    private val avaloirService: AvaloirService,
) : Worker(context, workerParameters) {

    companion object {
        const val WORK_RESULT = "work_result"
        val WORK_UUID = randomUUID()
    }

    private val manager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val outputData = Data.Builder().putString(WORK_RESULT, "Task Started")

    override fun doWork(): Result {
        val taskData = inputData
        val taskDataString = taskData.getString(AvaloirSyncScreen.MESSAGE_STATUS)
        val notificationString = taskData.getString(AvaloirSyncScreen.MESSAGE_STATUS)

        uploadContent()
        sleep(15)
        downloadContent()

        outputData.putString(WORK_RESULT, "Task Finished").build()
        return Result.success(outputData.build())
    }

    private fun downloadContent() {

        var result = downloadAvaloirs()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_downloAvaloirs", 4,
                "downloAvaloirs",
                "Avaloirs error: ${result.message}"
            )
            is NotificationState.Success -> showNotification(
                "message_channel_downloAvaloirs", 4,
                "downloAvaloirs",
                "Avaloirs téléchargés"
            )
        }

        result = downloadDates()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_downlodate", 5,
                "downlodates",
                "Dates error: ${result.message}"
            )
            is NotificationState.Success -> showNotification(
                "message_channel_downlodate", 5,
                "downlodates",
                "Dates téléchargées"
            )
        }

        result = downloadCommentaires()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_uploadCommentaires", 6,
                "uploadCommentaires",
                "Commentaires error: ${result.message}"
            )
            is NotificationState.Success -> showNotification(
                "message_channel_uploadCommentaires", 6,
                "uploadCommentaires",
                "Commentaires téléchargés"
            )
        }

    }

    private fun uploadContent() {

        var result = uploadAvaloir()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_uploadAvaloir",
                2,
                "uploadAvaloir",
                "Envoie des avaloirs erreur ${result.message}"
            )
            is NotificationState.Success -> showNotification(
                "message_channel_uploadAvaloir",
                2,
                "uploadAvaloir",
                "Avaloirs uploadés"
            )
            null -> {}
        }


        result = uploadDatesNettoyage()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_uploadDatesNettoyage", 3,
                "uploadDatesNettoyage",
                "Envoie des Dates erreur ${result.message}"
            )
            is NotificationState.Success -> showNotification(
                "message_channel_uploadDatesNettoyage", 3,
                "uploadDatesNettoyage",
                "Dates uploadés"
            )
            null -> {

            }
        }

        result = uploadCommentaires()
        when (result) {
            is NotificationState.Error -> showNotification(
                "message_channel_uploadCommentaires", 4,
                "uploadCommentaires",
                "Envoie des Commentaires erreur ${result.message}"
            )
            is NotificationState.Success -> showNotification(
                "message_channel_uploadCommentaires", 4,
                "uploadCommentaires",
                "Commentaires uploadés"
            )
            null -> {}
        }

    }

    private fun downloadAvaloirs(): NotificationState {
        try {
            val response = avaloirService.fetchAllAvaloirsNotSuspend()
            val res = response.execute()
            if (res.isSuccessful) {
                Timber.d("work avaloirs ${res.body()}")
                res.body()?.let {
                    try {
                        avaloirRepository.insertAvaloirsNotSuspend(it)
                        return NotificationState.Success("OK")
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        Timber.d("avaloirs error ${e.message}")
                        return NotificationState.Error("${e.message}")
                    }
                }
                return NotificationState.Success("OK")
            } else {
                Firebase.crashlytics.log("error download avaloirs ${res.code()} ${res.body()}")
                return NotificationState.Error("${res.body()}")
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            Timber.d("work err $e")
            return NotificationState.Error("${e.message}")
        }
    }

    private fun downloadDates(): NotificationState {
        try {
            val response = avaloirService.fetchAllDatesNotSuspend()
            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let {
                    try {
                        Timber.d("work dates ${it}")
                        avaloirRepository.insertDatesNotSuspend(it)
                        return NotificationState.Success("OK")
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        Timber.d("dates error ${e.message}")
                        return NotificationState.Error("${e.message}")
                    }
                }
                return NotificationState.Success("OK")
            } else {
                Firebase.crashlytics.log("error download dates ${res.code()} ${res.body()}")
                return NotificationState.Error("${res.body()}")
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            Timber.d("work dates err $e")
            return NotificationState.Error("${e.message}")
        }
    }

    private fun downloadCommentaires(): NotificationState {
        try {
            val response = avaloirService.fetchAllCommentairesNotSuspend()
            val res = response.execute()
            if (res.isSuccessful) {
                Timber.d("work commentaires ${res.body()}")
                res.body()?.let {
                    try {
                        avaloirRepository.insertCommentairesNotSuspend(it)
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        Timber.d("commentaires error ${e.message}")
                        return NotificationState.Error("${e.message}")
                    }
                }
                return NotificationState.Success("OK")
            } else {
                Timber.d("work commentaires ${res.body()}")
                Firebase.crashlytics.log("error download comments ${res.code()} ${res.body()}")
                return NotificationState.Error("${res.body()}")
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            Timber.d("work err $e")
            return NotificationState.Error("${e.message}")
        }
    }

    private fun uploadAvaloir(): NotificationState? {

        val fileHelper = FileHelper()

        avaloirRepository.getAllAvaloirsDraftsList().forEach { avaloirDraft ->

            val imgFile = File(avaloirDraft.imageUrl)
            val requestBody = fileHelper.createRequestBody(imgFile)
            val part = fileHelper.createPart(imgFile, requestBody)

            val coordinates = Coordinates(avaloirDraft.latitude, avaloirDraft.longitude)
            val response = avaloirService.insertAvaloirNotSuspend(coordinates, part, requestBody)

            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val avaloir = dataMessage.avaloir
                    if (avaloir.idReferent > 0) {
                        try {
                            avaloirRepository.deleteAvaloirDraftNotSuspend(avaloirDraft)
                        } catch (e: Exception) {
                            Firebase.crashlytics.recordException(e)
                            return NotificationState.Error("${e.message}")
                        }
                        return NotificationState.Success("OK")
                    }
                    return NotificationState.Error("OK")
                }
            } else {
                Firebase.crashlytics.log("error avaloir upload failed ${res.code()} ${res.body()}")
                Timber.d("avaloir upload failed ${response}")
                return NotificationState.Error("${res.body()}")
            }
        }

        return null
    }

    private fun uploadDatesNettoyage(): NotificationState? {

        avaloirRepository.getAllDatesNettoyagesDraftsList().forEach { dateNettoyage ->

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
            val date = format.format(dateNettoyage.createdAt)
            Timber.d("date $date")

            val response = avaloirService.insertDateNotSuspend(dateNettoyage.avaloirId, date)

            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val dateResult = dataMessage.date
                    if(dateResult.avaloirId > 0) {
                        try {
                            avaloirRepository.deleteDateNettoyageNotSuspend(dateNettoyage)
                        } catch (e: Exception) {
                            Firebase.crashlytics.recordException(e)
                            return NotificationState.Error("${e.message}")
                        }
                    }
                    return NotificationState.Success("OK")
                }
            } else {
                Firebase.crashlytics.log("error upload date ${res.code()} ${res.body()}")
                Timber.d("date failed ${response}")
                return NotificationState.Error("${res.body()}")
            }
        }
        return null
    }

    private fun uploadCommentaires(): NotificationState? {

        avaloirRepository.getAllCommentairessDraftsList().forEach { commentaire ->

            val response = avaloirService.insertCommentaireNotSuspend(
                commentaire.avaloirId,
                commentaire.content
            )

            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val commentaireResult = dataMessage.commentaire
                    if(commentaireResult.avaloirId > 0) {
                        try {
                            avaloirRepository.deleteCommentaireNotSuspend(commentaire)
                        } catch (e: Exception) {
                            Firebase.crashlytics.recordException(e)
                            return NotificationState.Error("${e.message}")
                        }
                    }
                    return NotificationState.Success("OK")
                }
            } else {
                Firebase.crashlytics.log("error upload commentaires ${res.code()} ${res.body()}")
                Timber.d("commentaire failed ${response}")
                return NotificationState.Error("${res.body()}")
            }
        }
        return null
    }

    private fun showNotification(id: String, idNotify: Int, name: String, desc: String) {
        val channelId = id
        val channelName = name

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("AppTravaux")
            .setContentText(desc)
            .setSmallIcon(R.drawable.ic_outline_notifications_active_24)

        manager.notify(idNotify, builder.build())
    }
}