package be.marche.apptravaux.avaloir.model

import android.app.Application
import androidx.lifecycle.*
import be.marche.apptravaux.api.TravauxService
import be.marche.apptravaux.avaloir.entity.*
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
        emit(avaloirRepository.getAll())
    }

    var resultSearch = MutableLiveData<SearchResponse>()

    fun setAvaloir(avaloir: Avaloir) {
        this.avaloir = liveData { emit(avaloir) }
    }

    fun getDatesFromServer(): LiveData<List<DateNettoyage>> = liveData {
        emit(avaloirRepository.getAllDatesFromApi())
    }

    fun getAllAvaloirsFromServer(): LiveData<List<Avaloir>> = liveData {
        emit(avaloirRepository.getAllAvaloirsFromApi())
    }

    fun getAll(): LiveData<List<Avaloir>> {
        return avaloirs
    }

    fun getAvaloirById(avaloirId: Int): LiveData<Avaloir> {
        return avaloirRepository.getById(avaloirId)
    }

    fun getDatesByAvaloirId(avaloirId: Int): LiveData<List<DateNettoyage>> {
        return avaloirRepository.getDatesByAvaloirId(avaloirId)
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

    fun insertDates(dates: List<DateNettoyage>) {
        viewModelScope.launch {
            avaloirRepository.insertDates(dates)
        }
    }

    fun insertAsync(avaloir: Avaloir, part: MultipartBody.Part, requestBody: RequestBody) {
        viewModelScope.launch {
            val response = travauxService.insertAvaloir(avaloir)
            val response2 = travauxService.uploadPhoto(avaloir.idReferent, part, requestBody)
            if (response.isSuccessful) {
                response.body()?.let { dataMessage ->
                    insertAvaloir(avaloir)
                    Timber.w("zeze insert sync " + dataMessage)
                }
            }
        }
    }

    fun saveAsync(avaloir: Avaloir) {
        viewModelScope.launch {
            val response = travauxService.updateAvaloir(avaloir.idReferent, avaloir)
            if (response.isSuccessful) {
                response.body()?.let { dataMessage ->
                    insertAvaloir(avaloir)
                    Timber.w("zeze update sync " + dataMessage)
                }
            }
        }
    }

    fun addCleaningDateAsync(avaloir: Avaloir, date: String) {
        viewModelScope.launch {
            val response = travauxService.cleanAvaloir(avaloir.idReferent, date, avaloir)
            if (response.isSuccessful) {
                response.body()?.let { dataMessage ->
                    if (dataMessage.error == 1) {

                    } else {
                        insertDates(listOf(dataMessage.date))
                    }
                }
            }
        }
    }

    fun search(latitude: Double, longitude: Double, distance: String) {
        viewModelScope.launch {
            val response =
                travauxService.searchAvaloir(SearchRequest(latitude, longitude, distance))
            if (response.isSuccessful) {
                response.body()?.let { searchResponse ->
                    Timber.w("zeze search " + searchResponse)
                    resultSearch.value = searchResponse
                }
            }
        }
    }

}