package be.marche.apptravaux.avaloir.model

import android.app.Application
import androidx.lifecycle.*
import be.marche.apptravaux.avaloir.api.AvaloirService
import be.marche.apptravaux.avaloir.entity.*
import be.marche.apptravaux.avaloir.repository.AvaloirRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AvaloirViewModel(
    application: Application,
    private val avaloirRepository: AvaloirRepository,
    private val avaloirService: AvaloirService
) :
    AndroidViewModel(application) {

    lateinit var coordinates: Coordinates
    var avaloir: MutableLiveData<Avaloir> = MutableLiveData<Avaloir>()

    private val avaloirs = liveData(Dispatchers.IO) {
        //emit(Result.loading())
        emit(avaloirRepository.getAll())
    }

    var resultSearch = MutableLiveData<SearchResponse>()

    fun changeValueCurrentAvaloir(avaloir: Avaloir) {
        this.avaloir.value = avaloir
    }

    fun registerCoordinates(latitude: Double, longitude: Double) {
        this.coordinates = Coordinates(
            latitude,
            longitude
        )
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

    fun insertAsync(coordinates: Coordinates, part: MultipartBody.Part, requestBody: RequestBody) {
        viewModelScope.launch {
            val response = avaloirService.insertAvaloir(coordinates, part, requestBody)
            if (response.isSuccessful) {
                response.body()?.let { dataMessage ->
                    val avaloir = dataMessage.avaloir
                    insertAvaloir(avaloir)
                    changeValueCurrentAvaloir(avaloir)
                }
            } else {
                //{"type":"https:\/\/tools.ietf.org\/html\/rfc2616#section-10","title":"An error occurred","status":500,"detail":"Internal Server Error"}

            }
        }
    }

    fun saveAsync(avaloir: Avaloir) {
        viewModelScope.launch {
            val response = avaloirService.updateAvaloir(avaloir.idReferent, avaloir)
            if (response.isSuccessful) {
                response.body()?.let { dataMessage ->
                    insertAvaloir(avaloir)
                    changeValueCurrentAvaloir(avaloir)
                }
            }
        }
    }

    fun addCleaningDateAsync(avaloir: Avaloir, date: String) {
        viewModelScope.launch {
            val response = avaloirService.cleanAvaloir(avaloir.idReferent, date, avaloir)
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
                avaloirService.searchAvaloir(SearchRequest(latitude, longitude, distance))
            if (response.isSuccessful) {
                response.body()?.let { searchResponse ->
                    resultSearch.value = searchResponse
                }
            }
        }
    }

}
