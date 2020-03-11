package be.marche.apptravaux.avaloir.model

import android.app.Application
import androidx.lifecycle.*
import be.marche.apptravaux.api.TravauxService
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.repository.AvaloirRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

class AvaloirViewModel(
    application: Application,
    private val avaloirRepository: AvaloirRepository,
    private val travauxService: TravauxService
) :
    AndroidViewModel(application) {

    lateinit var avaloir: LiveData<Avaloir>

    private val avaloirs = liveData(Dispatchers.IO) {
        val avaloirs = avaloirRepository.getAll()
        emit(avaloirs)
    }

    fun setAvaloir(avaloir: Avaloir) {
        this.avaloir = liveData { emit(avaloir) }
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

    fun uploadImage(avaloir: Avaloir, part: MultipartBody.Part, requestBody: RequestBody) {
        viewModelScope.launch {
            travauxService.uploadPhoto(avaloir.idReferent, part, requestBody)
        }
    }
}