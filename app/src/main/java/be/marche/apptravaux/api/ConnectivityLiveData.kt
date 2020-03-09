package be.marche.apptravaux.api

import android.app.Application
import android.content.Context
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData

class ConnectivityLiveData(application: Application) : LiveData<Boolean>() {

    private val networkRequest: NetworkRequest
    private val connectivityManager: ConnectivityManager

    init {
        connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkRequest =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
    }

    private fun checkNetworkState(): Boolean {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    override fun onActive() {
        super.onActive()

        value = checkNetworkState()
    }
}

