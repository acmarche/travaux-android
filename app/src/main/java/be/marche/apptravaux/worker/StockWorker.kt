package be.marche.apptravaux.worker

import android.content.Context
import android.os.SystemClock.sleep
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import be.marche.apptravaux.entities.NotificationState
import be.marche.apptravaux.networking.StockService
import be.marche.apptravaux.repository.StockRepository
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class StockWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val stockRepository: StockRepository,
    private val stockService: StockService,
) : CoroutineWorker(context, workerParameters) {

    private val outputData = Data.Builder().putString(WORK_RESULT, "Synchronisation démarrée")

    companion object {
        const val WORK_RESULT = "work_result"
    }

    override suspend fun doWork(): Result {
        upload()
        sleep(150)
        download()
        outputData.putString(AvaloirAsyncWorker.WORK_RESULT, "Synchronisation finie").build()
        return Result.success(outputData.build())
    }

    private suspend fun upload(): NotificationState {
        try {
            val drafts = stockRepository.getAllQuantitesDraftsList()
            drafts.forEach { draft ->
                val response = stockService.updateProduit(draft.produit_id, draft.quantite)

                if (response.isSuccessful) {
                    stockRepository.deleteQuantiteDraft(draft)
                } else {
                    Firebase.crashlytics.log("error download stock ${response.code()} ${response.body()}")
                    NotificationState.Error("${response.body()}")
                }
            }
            return NotificationState.Success("OK")
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
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
                return NotificationState.Success("OK")
            } else {
                Firebase.crashlytics.log("error download stock ${response.code()} ${response.body()}")
                return NotificationState.Error("${response.body()}")
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            return NotificationState.Error("${e.message}")
        }
    }
}