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

    private val avaloirs = liveData(Dispatchers.IO) {
        val avaloirs = avaloirRepository.getAll()
        emit(avaloirs)
    }

    val avaloirsFromFlux: LiveData<List<Avaloir>> = liveData {
        emit(avaloirRepository.getAllAvaloirsFromApi())
    }

    fun getAllAvaloirsFromFlux(): LiveData<List<Avaloir>> {
        return avaloirsFromFlux
    }

    fun getAll(): LiveData<List<Avaloir>> {
        return avaloirs
    }

    fun getAvaloir(): LiveData<Avaloir> {
        val avaloir = liveData(Dispatchers.IO) {
            val emps = Avaloir(null, 22, 10.0, 10.0)
            emit(emps)
        }
        return avaloir
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
}