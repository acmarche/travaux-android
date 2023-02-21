package be.marche.apptravaux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.WorkInfo
import be.marche.apptravaux.location.GeolocationServiceViewModel
import be.marche.apptravaux.navigation.Navigation
import be.marche.apptravaux.networking.NetworkUtils
import be.marche.apptravaux.screens.SyncScreen
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.viewModel.ErrorViewModel
import be.marche.apptravaux.viewModel.StockViewModel
import be.marche.apptravaux.viewModel.WorkerViewModel
import be.marche.apptravaux.worker.AvaloirAsyncWorker
import be.marche.apptravaux.worker.StockWorker
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val avaloirViewModel: AvaloirViewModel by viewModels()
    private val locationViewModel: GeolocationServiceViewModel by viewModels()
    private val stockViewModel: StockViewModel by viewModels()
    private val workerViewModel: WorkerViewModel by viewModels()
    private val errorViewModel: ErrorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetworkUtils.getNetworkLiveData(applicationContext).observe(this) {
            if (it) {
                lifecycleScope.launch {
                    syncContent()
                }
            } else {
                try {
                    workerViewModel.cancelWork(WorkerViewModel.SYNC_WORK_TAG)
                } catch (ex: Exception) {

                }
            }
        }

        setContent {
            AppTravaux6Theme {
                Navigation(
                    avaloirViewModel,
                    locationViewModel,
                    stockViewModel,
                    workerViewModel,
                    errorViewModel
                )
            }
        }
    }

    private fun syncContent() {
        val workerAvaloir = workerViewModel.workManager
        val taskData =
            Data.Builder().putString(SyncScreen.MESSAGE_STATUS_AVALOIR, "Notification Done.")
                .build()
        val requestAvaloir = workerViewModel.createRequest(
            taskData,
            AvaloirAsyncWorker::class.java,
            WorkerViewModel.SYNC_WORK_TAG
        )
        workerViewModel.enqueueWorkRequest(
            requestAvaloir,
            WorkerViewModel.AVALOIR_SYNC_WORK_REQUEST
        )

        workerAvaloir.getWorkInfoByIdLiveData(requestAvaloir.id).observe(this) {
            if (it != null) {
                when (it.state) {
                    WorkInfo.State.FAILED -> {
                        Firebase.crashlytics.log("Failt auto sync avaloir ${it.state}")
                    }
                    else -> {

                    }
                }
            }
        }

        val requestStock =
            workerViewModel.createRequest(
                taskData,
                StockWorker::class.java,
                WorkerViewModel.SYNC_WORK_TAG
            )
        workerViewModel.enqueueWorkRequest(requestStock, WorkerViewModel.STOCK_SYNC_WORK_REQUEST)

        workerAvaloir.getWorkInfoByIdLiveData(requestStock.id).observe(this) {
            if (it != null) {
                when (it.state) {
                    WorkInfo.State.FAILED -> {
                        Firebase.crashlytics.log("Failt auto sync stock ${it.state}")
                    }
                    else -> {

                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewConversation() {
        AppTravaux6Theme {

        }
    }
}
