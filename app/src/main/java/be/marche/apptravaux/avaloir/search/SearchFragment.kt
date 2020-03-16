package be.marche.apptravaux.avaloir.search

import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.Coordinates
import be.marche.apptravaux.avaloir.list.AvaloirListAdapter
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirSearchBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class SearchFragment : Fragment(), AvaloirListAdapter.AvaloirListAdapterListener {

    private val REQUEST_CHECK_SETTINGS: Int = 1
    private var _binding: FragmentAvaloirSearchBinding? = null
    private val binding get() = _binding!!
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var listener: AvaloirListAdapter.AvaloirListAdapterListener? = null
    var currentLocation: Location? = null

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

        binding.btnAddAvaloir.setOnClickListener {
            registerCoordinates()
            findNavController().navigate(R.id.action_searchFragment_to_addFragment)
        }

        initRecycler()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 25000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    currentLocation = location
                }
                updateUi()
                makeSearch()
            }
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                handleLastLocation(location)
                currentLocation = location
            }
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAvaloirSelected(avaloir: Avaloir) {
        avaloirModel.changeValueCurrentAvaloir(avaloir)
        findNavController().navigate(R.id.action_searchFragment_to_showFragment)
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

    private fun updateUi() {
        currentLocation.let { location ->
            location.apply {
                binding.locationTextView.text = requireContext().getString(
                    R.string.avaloir_location,
                    location?.latitude.toString(),
                    location?.longitude.toString()
                )
            }
        }
    }

    private fun makeSearch() {
        currentLocation.let { location ->
            location.apply {
                avaloirModel.search(this!!.latitude, this.longitude, "500km")
                avaloirModel.resultSearch.observe(
                    viewLifecycleOwner,
                    Observer { searchResponse ->
                        val avaloirs = searchResponse.avaloirs
                        avaloirs.let { adapter.setAvaloirs(avaloirs) }
                    })
            }
        }
    }

    private fun initRecycler() {
        listener = this
        val recyclerView = binding.recyclerViewAvaloirs
        adapter = requireContext().let { AvaloirListAdapter(listener) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun registerCoordinates() {
        avaloirModel.coordinates = Coordinates(
            currentLocation!!.latitude,
            currentLocation!!.longitude
        )
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