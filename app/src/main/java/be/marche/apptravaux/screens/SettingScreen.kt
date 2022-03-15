package be.marche.apptravaux.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import be.marche.apptravaux.screens.widgets.CardRow
import be.marche.apptravaux.worker.StockWorker
import timber.log.Timber

class SettingScreen(val navController: NavController) {

    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        val a = CardData(
            "Local data"
        ) {
            Toast.makeText(
                context,
                "Non implémenté",
                Toast.LENGTH_LONG
            ).show()
        }
        val cards: List<CardData> = listOf(a)

        MainContentHome(datas = cards)
    }

    @Composable
    fun Content(
        datas: List<CardData>
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Paramètres",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                LazyColumn {
                    items(datas) { data ->
                        CardRow(data.texte, data.action)
                    }
                }
            }
        }
    }

    @Composable
    fun WorkerTest() {
        val context = LocalContext.current
        Timber.d("MainScreen")
        val request: WorkRequest =
            OneTimeWorkRequestBuilder<StockWorker>()
                .build()

        val lifeCycle = LocalLifecycleOwner.current
        val workManager = WorkManager.getInstance(context)

        /*     workManager.getWorkInfoByIdLiveData(request.id).observe(lifeCycle) { workInfo ->
                 workInfo.let {
                     if (workInfo != null) {
                         val progress = workInfo.progress
                         val value = progress.getInt(Progress, 0)
                         Timber.d("value progress $value")
                     }
                 }
             }*/

        workManager
            // requestId is the WorkRequest id
            .getWorkInfoByIdLiveData(request.id)
            .observe(lifeCycle, Observer { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    //   val progress = workInfo.progress
                    //  val value = progress.getInt(Progress, 0)
                    val progress = workInfo.progress.getInt(StockWorker.Progress, -1)
                    Timber.d("value progress2 $progress")
                    // Do something with progress information
                }
            })

    }
}