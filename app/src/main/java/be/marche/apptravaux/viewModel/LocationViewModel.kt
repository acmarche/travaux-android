package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.marche.apptravaux.location.LocationService
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import com.google.android.libraries.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class LocationViewModel @Inject constructor(
    private val locationService: LocationService,
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    init {
        Timber.d("init locationViewModel")
        if (locationEnabled()) {
            launch()
        }
    }

    fun launch() {
        locationService.getDeviceLocation(applicationContext)
    }

    fun locationEnabled(): Boolean {
        return locationService.locationEnabled(applicationContext)
    }

    /**
     * LOCATION
     */
    private var _locationPermissionGranted = MutableLiveData(true)
    var locationPermissionGranted: LiveData<Boolean> = _locationPermissionGranted

    private var _userCurrentStateLatLng = mutableStateOf(LatLng(0.0, 0.0))
    var userCurrentStateLatLng: MutableState<LatLng> = _userCurrentStateLatLng

    var currentLatLng: LatLng = LatLng(0.0, 0.0)


}
