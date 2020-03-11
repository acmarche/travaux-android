package be.marche.apptravaux.avaloir.model

import android.app.Application
import androidx.lifecycle.*
import be.marche.apptravaux.api.TravauxService
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.repository.AvaloirRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AvaloirViewModel(
    application: Application,
    private val avaloirRepository: AvaloirRepository,
    private val travauxService: TravauxService
) :
    AndroidViewModel(application) {

    lateinit var avaloir: MutableLiveData<Avaloir>

    private val avaloirs = liveData(Dispatchers.IO) {
        val avaloirs = avaloirRepository.getAll()
        emit(avaloirs)
    }

    fun setAvaloir(avaloir: Avaloir) {
        val live = MutableLiveData<Avaloir>()
        live.value = avaloir
    }

    val avaloirsFromFlux: LiveData<List<Avaloir>> = liveData {
        emit(avaloirRepository.getAllAvaloirsFromApi())
    }

    fun getAllAvaloirsFromServer(): LiveData<List<Avaloir>> {
        return avaloirsFromFlux
    }

    fun getAll(): LiveData<List<Avaloir>> {
        return avaloirs
    }

    fun getAvaloirById(avaloirId: Int): LiveData<Avaloir> {
        return avaloirRepository.getById(avaloirId)
    }

    fun insertAvaloir(avaloir: Avaloir) {
        viewModelScope.launch {
            avaloirRepository.insertAvaloirs(listOf(avaloir))
        }
    }

    fun insertAvaloirs(avaloirs: List<Avaloir>) {
        viewModelScope.launch {
            avaloirRepository.insertAvaloirs(avaloirs)
        }
    }

    fun saveAsync(avaloir: Avaloir) {
        viewModelScope.launch {
            val response = travauxService.updateAvaloir(avaloir.idReferent, avaloir)
            Timber.w("zeze response: " + response)
            if (response.isSuccessful) {
                response.body()?.let { message ->
                    Timber.w("zeze update sync " + message)
                }
            }
        }
    }

     fun cleanAsync(avaloir: Avaloir, date: String) {
        viewModelScope.launch {
            val response = travauxService.cleanAvaloir(avaloir.idReferent, date, avaloir)
            Timber.w("zeze response: " + response)
            if (response.isSuccessful) {
                response.body()?.let { message ->
                    Timber.w("zeze update sync " + message)
                }
            }
        }
    }
}