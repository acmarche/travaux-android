package be.marche.apptravaux.worker

import android.content.Context
import android.os.SystemClock.sleep
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import be.marche.apptravaux.entities.NotificationState
import be.marche.apptravaux.networking.StockService
import be.marche.apptravaux.repository.StockRepository
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@HiltWorker
class StockWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val stockRepository: StockRepository,
    private val stockService: StockService,
) : CoroutineWorker(context, workerParameters) {

    private val outputData = Data.Builder().putString(WORK_RESULT, "Synchronisation démarrée")

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 5L
        const val WORK_RESULT = "work_result"
    }

    override suspend fun doWork(): Result {
        upload()
        sleep(150)
        download()
        outputData.putString(AvaloirSyncWorker.WORK_RESULT, "Synchronisation finie").build()
        return Result.success(outputData.build())
    }

    private suspend fun upload(): NotificationState {
        try {
            val drafts = stockRepository.getAllQuantitesDraftsList()
            drafts.forEach { draft ->
                val response = stockService.updateProduit(draft.produit_id, draft.quantite)

                if (response.isSuccessful) {
                    stockRepository.deleteQuantiteDraft(draft)
                    return NotificationState.Success("OK")
                } else {
                    Firebase.crashlytics.log("error download stock ${response.code()} ${response.body()}")
                }
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }

        return NotificationState.Error("xx")
    }

    private suspend fun download(): NotificationState {
        try {
            val response = stockService.getAllData()
            if (response.isSuccessful) {
                response.body()?.let { stockData ->
                    try {
                        stockRepository.insertCategories(stockData.categories)
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                    }
                    try {
                        stockRepository.insertProduits(stockData.produits)
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                    }
                }
            } else {
                Firebase.crashlytics.log("error download stock ${response.code()} ${response.body()}")
            }

            //    Firebase.crashlytics.log("error download avaloirs ${response.code()} ${res.body()}")
            //    return NotificationState.Error("${response.body()}")
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }

        return NotificationState.Error("xx")
    }


    suspend fun t(): Result {
        val firstUpdate = workDataOf(Progress to 0)
        val lastUpdate1 = workDataOf(Progress to 30)
        val lastUpdate2 = workDataOf(Progress to 60)
        val lastUpdate = workDataOf(Progress to 100)
        setProgress(firstUpdate)
        delay(delayDuration)
        setProgress(lastUpdate1)
        delay(delayDuration)
        setProgress(lastUpdate2)
        delay(delayDuration)
        setProgress(lastUpdate)
        return Result.success()
    }

    suspend fun x(): Result {
        for (x in 1..100) {
            val progressData = workDataOf(Progress to x)
            setProgress(progressData)

            // do something
            try {
                delay(4000)

            } catch (e: CancellationException) {
            }

            if (isStopped) {
                return Result.success(workDataOf(Progress to x))
            }
        }
        return Result.success(workDataOf(Progress to 100))
    }

}