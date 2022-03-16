package be.marche.apptravaux.screens.stock

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.StockViewModel
import be.marche.apptravaux.worker.StockWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi

class StockSyncScreen(
    val navController: NavController
) {
    lateinit var worker: WorkManager

    companion object {
        const val MESSAGE_STATUS = "message_status"
        const val NOTIFICATION_MESSAGE = "message notif"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    fun SyncContent(
        stockViewModel: StockViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Synchronisation des stocks",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.StockHomeScreen.route)
                            }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    },
                    backgroundColor = Colors.Pink500,
                    elevation = AppBarDefaults.TopAppBarElevation
                )
            }
        ) {
            val lifeCycle = LocalLifecycleOwner.current
            worker = stockViewModel.workManager
            val textInput = remember { mutableStateOf("") }
            val taskData = Data.Builder().putString(MESSAGE_STATUS, "Notification Done.").build()
            val request = stockViewModel.createRequest(taskData)

            worker.getWorkInfoByIdLiveData(request.id).observe(lifeCycle) { workInfo ->
                workInfo.let {
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

            val connection by connectivityState()
            val isConnected = connection == ConnectionState.Available

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ConnectivityStatusBox(isConnected)
                Content(stockViewModel, request, textInput, isConnected)
            }
        }
    }

    @Composable
    private fun Content(
        stockViewModel: StockViewModel,
        request: OneTimeWorkRequest,
        textInput: MutableState<String>,
        isConnected: Boolean
    ) {
        Text(text = stringResource(R.string.sync_stock))
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
        OutlinedButtonJf(
            "Synchroniser les données", isConnected
        ) {
            stockViewModel.enqueueWorkRequest(request)
        }
        Spacer(modifier = Modifier.height(30.dp))

        Text(text = textInput.value)
    }
}