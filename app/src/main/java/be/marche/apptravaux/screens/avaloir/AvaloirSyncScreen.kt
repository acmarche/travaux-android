package be.marche.apptravaux.screens.avaloir

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
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
import be.marche.apptravaux.screens.widgets.TopAppBarJf
import be.marche.apptravaux.ui.theme.MEDIUM_PADDING
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.worker.AvaloirSyncWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi

class AvaloirSyncScreen(
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
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        Scaffold(
            topBar = {
                TopAppBarJf(
                    "Synchronisation des avaloirs"
                ) { navController.navigate(TravauxRoutes.AvaloirHomeScreen.route) }
            }
        ) {
            val lifeCycle = LocalLifecycleOwner.current
            worker = avaloirViewModel.workManager
            val textInput = remember { mutableStateOf("") }
            val taskData = Data.Builder().putString(MESSAGE_STATUS, "Notification Done.").build()
            val request = avaloirViewModel.createRequest(taskData)

            worker.getWorkInfoByIdLiveData(request.id).observe(lifeCycle) { workInfo ->
                workInfo.let {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val outputData = it.outputData
                            val taskResult = outputData.getString(AvaloirSyncWorker.WORK_RESULT)
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
                Content(avaloirViewModel, request, textInput, isConnected)
            }
        }
    }

    @Composable
    private fun Content(
        avaloirViewModel: AvaloirViewModel,
        request: OneTimeWorkRequest,
        textInput: MutableState<String>,
        isConnected: Boolean
    ) {
        Text(text = stringResource(R.string.sync_intro1))
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )
        Text(text = stringResource(R.string.sync_intro2))
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
            avaloirViewModel.enqueueWorkRequest(request)
        }
        Spacer(modifier = Modifier.height(30.dp))

        Text(text = textInput.value)
    }
}