package be.marche.apptravaux.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.UiState
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.repository.AvaloirRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val avaloirRepository: AvaloirRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel(

) {

    val itemsFlow = flow {
        val items = MutableList((1..100).random()) { index ->
            index
        }
        emit(items)
    }

    ///loop infinitive
    fun getItems() = flow {
        val items = MutableList((1..100).random()) { index ->
            index
        }
        emit(items)
    }

    var avaloir = MutableStateFlow<Avaloir?>(null)
        private set

    private val _uiState2 = MutableLiveData<UiState>(UiState.SignedOut)
    val uiState2: LiveData<UiState>
        get() = _uiState2


    fun makeFlow(): Flow<Int> = flow {
        repeat(3) { num ->
            delay(1000)
            emit(num)
        }
    }

    suspend fun main() {
        makeFlow()
            .collect { println(it) }
    }

    fun findByIdT(avaloirId: Int) {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            avaloir.value = avaloirRepository.findAvaloirById(avaloirId)
        }
    }
}