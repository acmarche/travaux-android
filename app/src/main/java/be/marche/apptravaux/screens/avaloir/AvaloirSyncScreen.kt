package be.marche.apptravaux.screens.avaloir

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import be.marche.apptravaux.navigation.TravauxRoutes
import be.marche.apptravaux.ui.theme.Colors
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.worker.AvaloirSyncWorker

class AvaloirSyncScreen(
    val navController: NavController
) {

    companion object {
        const val MESSAGE_STATUS = "message_status"
    }

    @Composable
    fun SyncContent(
        avaloirViewModel: AvaloirViewModel = viewModel()
    ) {
        lateinit var worker: WorkManager
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Sync des avaloirs",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(TravauxRoutes.AvaloirHomeScreen.route)
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
            val context = LocalContext.current
            val lifeCycle = LocalLifecycleOwner.current
            worker = WorkManager.getInstance(context)
            val textInput = remember { mutableStateOf("") }

            val powerConstraints = Constraints.Builder().setRequiresCharging(true).build()
            val taskData = Data.Builder().putString(MESSAGE_STATUS, "Notification Done.").build()
            val request = OneTimeWorkRequest.Builder(AvaloirSyncWorker::class.java)
                .setConstraints(powerConstraints).setInputData(taskData).build()

            worker.getWorkInfoByIdLiveData(request.id).observe(lifeCycle) { workInfo ->
                workInfo.let {
                    if (it.state.isFinished) {
                        val outputData = it.outputData
                        val taskResult = outputData.getString(AvaloirSyncWorker.WORK_RESULT)
                        if (taskResult != null) {
                            textInput.value = taskResult
                        }
                    } else {
                        val workStatus = workInfo.state
                        textInput.value = workStatus.toString()
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    worker.enqueue(request)
                }) {
                    Text(text = "Submit")
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(text = textInput.value)
            }
        }
    }
}