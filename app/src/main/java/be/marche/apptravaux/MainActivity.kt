package be.marche.apptravaux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import be.marche.apptravaux.navigation.Navigation
import be.marche.apptravaux.networking.NetworkUtils
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.viewModel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val avaloirViewModel: AvaloirViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetworkUtils.getNetworkLiveData(applicationContext).observe(this) {
            if (it) {
                lifecycleScope.launch {
            //        syncContent()
                }
            }
        }

        setContent {
            AppTravaux6Theme {
                Navigation(avaloirViewModel, locationViewModel)
            }
        }
    }

    private suspend fun syncContent() {
        avaloirViewModel.allAvaloirs.collect {
            avaloirViewModel.insertAvaloirs(it)
        }
    }

    @Preview
    @Composable
    fun PreviewConversation() {
        AppTravaux6Theme {

        }
    }
}
