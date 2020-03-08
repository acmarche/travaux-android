package be.marche.apptravaux.location

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveJf(application)
    //private val locationData = LocationLiveWithUtil(application)

    fun getLocationData() = locationData
}