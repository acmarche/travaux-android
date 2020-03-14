package be.marche.apptravaux.avaloir.search

import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.SearchResponse
import be.marche.apptravaux.avaloir.list.AvaloirListAdapter
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirSearchBinding
import be.marche.apptravaux.location.LocationUtil
import be.marche.apptravaux.location.LocationViewModel
import be.marche.apptravaux.permission.PermissionUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class SearchFragment : Fragment(), AvaloirListAdapter.AvaloirListAdapterListener {

    private val REQUEST_CHECK_SETTINGS: Int = 1
    private var _binding: FragmentAvaloirSearchBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel: LocationViewModel by sharedViewModel()
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var listener: AvaloirListAdapter.AvaloirListAdapterListener? = null
    var currentLocation: Location? = null

    //var currentLocation: MutableLiveData<Location>
    private lateinit var avaloirNew: Avaloir
    lateinit var liveSearchResult: MutableLiveData<SearchResponse>
    lateinit var adapter: AvaloirListAdapter
    lateinit var locationRequest: LocationRequest
    val requestingLocationUpdates = true
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.w("zeze create activity")
        binding.btnAddAvaloir.setOnClickListener {
            add()
            findNavController().navigate(R.id.action_searchFragment_to_addFragment)
        }

        //map renvoie une valeur
        //switch map renvoie un live

        initRecycler()

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Timber.w("zeze " + location)
                    currentLocation = location
                }
            }
        }

        Timber.w("zeze create client")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                handleLastLocation(location)
                currentLocation = location
            }
    }

    override fun onResume() {
        super.onResume()
        Timber.w("zeze start")
        if (requestingLocationUpdates) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun handleLastLocation(location: Location?) {

    }

    private fun handleLastLocation22(location: Location?) {
        Timber.w("zeze location ${location?.latitude}, ${location?.longitude}")
        currentLocation.let {
            location.apply {
                binding.locationTextView.text = requireContext().getString(
                    R.string.avaloir_location,
                    location?.latitude.toString(),
                    location?.longitude.toString()
                )
                avaloirModel.search(this!!.latitude, this!!.longitude, "500km")
                avaloirModel.resultSearch.observe(
                    viewLifecycleOwner,
                    Observer { searchResponse ->
                        Timber.w("zeze live search " + searchResponse)
                        val avaloirs = searchResponse.avaloirs
                        avaloirs.let { adapter.setAvaloirs(avaloirs) }
                    })
            }

        }
    }

    override fun onAvaloirSelected(avaloir: Avaloir) {
        avaloirModel.setAvaloir(avaloir)
        findNavController().navigate(R.id.action_searchFragment_to_showFragment)
    }

    private fun initRecycler() {
        listener = this
        val recyclerView = binding.recyclerViewAvaloirs
        adapter = requireContext().let { AvaloirListAdapter(listener) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun add() {
        avaloirNew = Avaloir(
            null,
            222,
            currentLocation!!.latitude,
            currentLocation!!.longitude
        )
        avaloirModel.insertAvaloir(avaloirNew)
        avaloirModel.setAvaloir(avaloirNew)
    }

    private fun checkSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

}