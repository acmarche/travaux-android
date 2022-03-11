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

    override fun doWork(): Result {
        val taskData = inputData
        val taskDataString = taskData.getString(AvaloirSyncScreen.MESSAGE_STATUS)
        Timber.d("do work taskdata ${taskDataString}")
        val notificationString = taskData.getString(AvaloirSyncScreen.MESSAGE_STATUS)
        val outputData = Data.Builder().putString(WORK_RESULT, "Task Started")

        sleep(30)

        //     if (uploadContent()) {
        //         outputData.putString(WORK_RESULT, "Upload Finished").build()
        //         showNotification("message_channel_upload", "Upload all", taskDataString.toString())
        //      }

        if (downloadContent()) {
            outputData.putString(WORK_RESULT, "Task Finished").build()
            return Result.success(outputData.build())
        }

        outputData.putString(WORK_RESULT, "Task Fail").build()
        showNotification("message_channel_finish", "Finish all", taskDataString.toString())
        return Result.success(outputData.build())
    }

    private fun showNotification(id: String, name: String, desc: String) {
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

        manager.notify(1, builder.build())
    }

    private fun downloadContent(): Boolean {

        //    downloadAvaloirs()
        //    showNotification("message_channel_syncAvaloirs", "syncAvaloirs", "Avaloirs synchronisés")

        downloadDates()
        showNotification("message_channel_syncDates", "syncDates", "Dates synchronisés")

        /*     downloadCommentaires()
             showNotification(
                 "message_channel_syncCommentaires",
                 "syncCommentaires",
                 "Commentaires synchronisés"
             )*/

        return true
    }

    private fun uploadContent(): Boolean {

        uploadAvaloir()
        showNotification("message_channel_uploadAvaloir", "uploadAvaloir", "Avaloirs uploadés")

        uploadDatesNettoyage()
        showNotification(
            "message_channel_uploadDatesNettoyage",
            "uploadDatesNettoyage",
            "Dates uploadés"
        )

        uploadCommentaires()
        showNotification(
            "message_channel_uploadCommentaires",
            "uploadCommentaires",
            "Commentaires uploadés"
        )

        return true
    }

    private fun downloadAvaloirs(): Boolean {
        try {
            val response = avaloirService.fetchAllAvaloirsNotSuspend()
            val res = response.execute()
            if (res.isSuccessful) {
                Timber.d("work avaloirs ${res.body()}")
                res.body()?.let {
                    try {
                        avaloirRepository.insertAvaloirsNotSuspend(it)
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        Timber.d("avaloirs error ${e.message}")
                    }
                }
                return true
            } else {
                Firebase.crashlytics.log("error download avaloirs ${res.code()} ${res.body()}")
            }
            return false
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            Timber.d("work err $e")
        }
        return false
    }

    private fun downloadDates(): Boolean {
        try {
            val response = avaloirService.fetchAllDatesNotSuspend()
            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let {
                    try {
                        Timber.d("work dates ${it}")
                        avaloirRepository.insertDatesNotSuspend(it)
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        Timber.d("dates error ${e.message}")
                    }
                }
                return true
            } else {
                Firebase.crashlytics.log("error download dates ${res.code()} ${res.body()}")
            }
            return false
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            Timber.d("work dates err $e")
        }
        return false
    }

    private fun downloadCommentaires(): Boolean {
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
                    }

                }
                return true
            } else {
                Timber.d("work commentaires ${res.body()}")
                Firebase.crashlytics.log("error download comments ${res.code()} ${res.body()}")
            }
            return false
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            Timber.d("work err $e")
        }
        return false
    }

    private fun uploadAvaloir(): Boolean {

        val fileHelper = FileHelper()

        avaloirRepository.getAllAvaloirsDraftsList().forEach { avaloirDraft ->

            Timber.d("avaloir draft try ${avaloirDraft}")

            val imgFile = File(avaloirDraft.imageUrl)
            val requestBody = fileHelper.createRequestBody(imgFile)
            val part = fileHelper.createPart(imgFile, requestBody)

            val coordinates = Coordinates(avaloirDraft.latitude, avaloirDraft.longitude)
            val response = avaloirService.insertAvaloirNotSuspend(coordinates, part, requestBody)

            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val avaloir = dataMessage.avaloir
                    Timber.d("avaloir added ${avaloir}")
                    avaloirRepository.deleteAvaloirDraftNotSuspend(avaloirDraft)
                }
            } else {
                Firebase.crashlytics.log("error avaloir upload failed ${res.code()} ${res.body()}")
                Timber.d("avaloir upload failed ${response}")
                //{"type":"https:\/\/tools.ietf.org\/html\/rfc2616#section-10","title":"An error occurred","status":500,"detail":"Internal Server Error"}
            }
        }

        return true
    }

    private fun uploadDatesNettoyage(): Boolean {

        avaloirRepository.getAllDatesNettoyagesDraftsList().forEach { dateNettoyage ->

            Timber.d("avaloir draft try ${dateNettoyage}")

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
            val date = format.format(dateNettoyage.createdAt)
            Timber.d("date $date")

            val response = avaloirService.insertDateNotSuspend(dateNettoyage.avaloirId, date)

            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val dateReturn = dataMessage.date
                    Timber.d("date added ${dateReturn}")
                    //      avaloirRepository.deleteDateNettoyageNotSuspend(dateNettoyage)
                }
            } else {
                Firebase.crashlytics.log("error upload date ${res.code()} ${res.body()}")
                Timber.d("date failed ${response}")
            }
        }

        return true
    }

    private fun uploadCommentaires(): Boolean {

        avaloirRepository.getAllCommentairessDraftsList().forEach { commentaire ->

            val response = avaloirService.insertCommentaireNotSuspend(
                commentaire.avaloirId,
                commentaire.content
            )

            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val commentaireResult = dataMessage.commentaire
                    Timber.d("commentaire added ${commentaireResult}")
                    //     avaloirRepository.deleteCommentaireNotSuspend(commentaire)
                }
            } else {
                Firebase.crashlytics.log("error upload comment ${res.code()} ${res.body()}")
                Timber.d("commentaire failed ${response}")
            }
        }

        return true
    }
}