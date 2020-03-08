package be.marche.apptravaux.location

import android.content.Context
import androidx.lifecycle.MutableLiveData

class LocationLiveWithUtil(context: Context) : MutableLiveData<LocationData>() {

    var locationUtil = LocationUtil(context, this)

    override fun onInactive() {
        super.onInactive()
        locationUtil.removeLocationUpdates()
    }

    override fun onActive() {
        super.onActive()
        locationUtil.lastLocation()
    }

}