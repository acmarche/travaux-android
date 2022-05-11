package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.entities.ErrorLog
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.repository.ErrorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ErrorViewModel @Inject constructor(
    private val errorRepository: ErrorRepository,
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private val _allErrorsFlow = MutableStateFlow<List<ErrorLog>>(emptyList())
    val allErrorsFlow: StateFlow<List<ErrorLog>> = _allErrorsFlow

    init {
        fetchFromDb()
    }

    fun fetchFromDb() {
        _allErrorsFlow.value = emptyList()
        viewModelScope.launch {
            errorRepository.getAll().collect { dates ->
                _allErrorsFlow.value = dates
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            errorRepository.deleteAll()
            _allErrorsFlow.value = emptyList()
        }
    }

}
