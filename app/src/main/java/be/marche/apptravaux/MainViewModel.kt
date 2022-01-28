package be.marche.apptravaux

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.repository.TravauxRepository
import be.marche.apptravaux.ui.theme.TravauxUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MainViewModel @Inject constructor(
    private val repository: TravauxRepository,
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val uiState: StateFlow<WeatherUiState> = _uiState

    init {
        fetchWeather()
    }

    private fun fetchWeather() {
        _uiState.value = WeatherUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = repository.fetchWeather("2.5", "5");
                Log.d("ZEZE", "response: ${response.toString()}")

                /* _uiState.value = WeatherUiState.Loaded(
                     TravauxUiModel("jfs", "cool")
                 )*/

            } catch (ex: Exception) {
                Log.d("ZEZE", "error: ${ex.message}")
                onErrorOccurred()
            }
        }
    }

    private fun onQueryLimitReached() {
        _uiState.value = WeatherUiState.Error(
            applicationContext.getString(R.string.query_limit_reached)
        )
    }

    private fun onErrorOccurred() {
        _uiState.value = WeatherUiState.Error(
            applicationContext.getString(R.string.something_went_wrong)
        )
    }

    sealed class WeatherUiState {
        object Empty : WeatherUiState()
        object Loading : WeatherUiState()
        class Loaded(val data: TravauxUiModel) : WeatherUiState()
        class Error(val message: String) : WeatherUiState()
    }

    companion object {
        const val AUSTIN_LONG = "50.733330"
        const val AUSTIN_LAT = "5.266666"
    }
}