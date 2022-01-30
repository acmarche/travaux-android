package be.marche.apptravaux.screens.avaloir

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.marche.apptravaux.WeatherLoadedScreen
import be.marche.apptravaux.viewModel.AvaloirViewModel
import com.myricseptember.countryfactcomposefinal.widgets.ErrorDialog

@Composable
fun AvaloirListScreen(
    navController: NavController,
    avaloirViewModel: AvaloirViewModel
) {
    when (val state = avaloirViewModel.uiState.collectAsState().value) {
        is AvaloirViewModel.AvaloirUiState.Loading -> {
            Log.d("ZEZE", "loading")
        }
        is AvaloirViewModel.AvaloirUiState.Error -> {
            Log.d("ZEZE", "error")
            ErrorDialog(state.message)
        }
        is AvaloirViewModel.AvaloirUiState.Loaded -> {
            Log.d("ZEZE", "loaded")
            WeatherLoadedScreen(state.data)
        }
    }
}
