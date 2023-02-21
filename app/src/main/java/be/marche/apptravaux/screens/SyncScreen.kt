package be.marche.apptravaux.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import be.marche.apptravaux.R
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.networking.ConnectionState
import be.marche.apptravaux.networking.connectivityState
import be.marche.apptravaux.screens.widgets.ConnectivityStatusBox
import be.marche.apptravaux.screens.widgets.OutlinedButtonJf
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.LARGEST_PADDING
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.ui.theme.ScreenSizeTheme
import be.marche.apptravaux.utils.DownloadHelper
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.viewModel.StockViewModel
import be.marche.apptravaux.viewModel.WorkerViewModel
import be.marche.apptravaux.worker.AvaloirAsyncWorker
import be.marche.apptravaux.worker.StockWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SyncScreen(
    val navController: NavController
) {
    lateinit var worker: WorkManager

    companion object {
        const val MESSAGE_STATUS_AVALOIR = "message_status_avaloir"
        const val MESSAGE_STATUS_STOCK = "message_status_statut"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    fun MaintContent(
        workerViewModel: WorkerViewModel = viewModel(),
        avaloirViewModel: AvaloirViewModel,
        stockViewModel: StockViewModel
    ) {
        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Synchronisation des données"
                ) { navController.navigate(TravauxRoutes.HomeScreen.route) }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

                val lifeCycle = LocalLifecycleOwner.current
                worker = workerViewModel.workManager
                val textInputAvaloir = remember { mutableStateOf("") }
                val textInputStock = remember { mutableStateOf("") }
                val taskDataAvaloir =
                    Data.Builder().putString(MESSAGE_STATUS_AVALOIR, "Notification Done.").build()
                val taskDataStock =
                    Data.Builder().putString(MESSAGE_STATUS_STOCK, "Notification Done.").build()
                val connection by connectivityState()
                val isConnected = connection == ConnectionState.Available
                val requestAvaloir = workerViewModel.createRequest(
                    taskDataAvaloir,
                    AvaloirAsyncWorker::class.java,
                    "avaloirSync"
                )
                val requestStock = workerViewModel.createRequest(
                    taskDataStock,
                    StockWorker::class.java,
                    "StockSync"
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    ConnectivityStatusBox(isConnected)
                    Spacer(modifier = Modifier.padding(5.dp))
                    Stats(avaloirViewModel, stockViewModel)
                    Spacer(modifier = Modifier.padding(5.dp))
                    TextesExplicatifs()
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(text = textInputAvaloir.value)
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(text = textInputStock.value)

                    Divider(
                        modifier = Modifier.height(MEDIUM_PADDING),
                        color = MaterialTheme.colors.background
                    )

                    OutlinedButtonJf(
                        "Synchroniser les données", isConnected
                    ) {
                        workerViewModel.enqueueWorkRequest(
                            requestAvaloir,
                            WorkerViewModel.AVALOIR_SYNC_WORK_REQUEST
                        )
                        workerViewModel.enqueueWorkRequest(
                            requestStock,
                            WorkerViewModel.STOCK_SYNC_WORK_REQUEST
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(
                        modifier = Modifier.height(MEDIUM_PADDING),
                        color = MaterialTheme.colors.background
                    )

                    syncStatutAvaloir(textInputAvaloir, lifeCycle, requestStock)
                    syncStatutStock(textInputStock, lifeCycle, requestAvaloir)
                }
            }
        }
    }

    private @Composable
    fun TextesExplicatifs() {
        Text(
            text = stringResource(R.string.sync_intro1),
            fontWeight = FontWeight.Bold,
            fontSize = ScreenSizeTheme.textStyle.fontWidth_1
        )
        Divider(
            modifier = Modifier.height(LARGEST_PADDING),
            color = MaterialTheme.colors.background
        )
        Text(text = stringResource(R.string.sync_intro2))
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
        Text(text = stringResource(R.string.sync_intro3))
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
    }

    private @Composable
    fun Stats(
        avaloirViewModel: AvaloirViewModel,
        stockViewModel: StockViewModel
    ) {
        val context = LocalContext.current
        val downloadHelper = DownloadHelper(context)

        LaunchedEffect(true) {
            avaloirViewModel.countAvaloirs()
            stockViewModel.countProduit()
            avaloirViewModel.countDatesNettoyages()
            avaloirViewModel.countCommentaire()
        }

        val statAvaloirs: String = java.lang.String.format(
            stringResource(R.string.stat_avaloirs),
            avaloirViewModel._countAvaloirs,
        )
        val statCommentaires: String = java.lang.String.format(
            stringResource(R.string.stat_commentaires),
            avaloirViewModel._countCommentaire,
        )
        val statDatesNettoyages: String = java.lang.String.format(
            stringResource(R.string.stat_dates_nettoyages),
            avaloirViewModel._countDateNettoyage,
        )

        val filesCount = downloadHelper.countFiles()
        val statImages: String = java.lang.String.format(
            stringResource(R.string.stat_images),
            filesCount,
        )

        val statProduits: String = java.lang.String.format(
            stringResource(R.string.stat_produits),
            stockViewModel._countProduit,
        )

        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = statAvaloirs,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = statImages,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = statDatesNettoyages,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = statCommentaires,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = statProduits,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
        )
    }

    private fun syncStatutAvaloir(
        textInput: MutableState<String>,
        lifeCycle: LifecycleOwner,
        request: OneTimeWorkRequest
    ) {
        worker.getWorkInfoByIdLiveData(request.id).observe(lifeCycle) { workInfo ->
            workInfo.let {
                if (it != null) {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val outputData = it.outputData
                            val taskResult = outputData.getString(AvaloirAsyncWorker.WORK_RESULT)
                            if (taskResult != null) {
                                textInput.value = taskResult
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            val workStatus = workInfo.state
                            textInput.value = workStatus.toString()
                        }
                        WorkInfo.State.RUNNING -> {
                            val workStatus = workInfo.state
                            textInput.value = workStatus.toString()
                        }
                        WorkInfo.State.CANCELLED -> {
                            val workStatus = workInfo.state
                            textInput.value = workStatus.toString()
                        }
                        else -> {
                            val workStatus = workInfo.state
                            textInput.value = workStatus.toString()
                        }
                    }
                }
            }
        }
    }

    private fun syncStatutStock(
        textInput: MutableState<String>,
        lifeCycle: LifecycleOwner,
        request: OneTimeWorkRequest
    ) {
        worker.getWorkInfoByIdLiveData(request.id).observe(lifeCycle) { workInfo ->
            workInfo.let {
                if (it != null) {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val outputData = it.outputData
                            val taskResult = outputData.getString(StockWorker.WORK_RESULT)
                            if (taskResult != null) {
                                textInput.value = taskResult
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            val workStatus = workInfo.state
                            textInput.value = workStatus.toString()
                        }
                        WorkInfo.State.RUNNING -> {
                            val workStatus = workInfo.state
                            textInput.value = workStatus.toString()
                        }
                        else -> {
                            val workStatus = workInfo.state
                            textInput.value = workStatus.toString()
                        }
                    }
                }
            }
        }
    }
}