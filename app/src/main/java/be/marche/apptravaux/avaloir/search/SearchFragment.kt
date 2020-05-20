package be.marche.apptravaux.avaloir.search

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.list.AvaloirListAdapter
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirSearchBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchFragment : Fragment(), AvaloirListAdapter.AvaloirListAdapterListener {

    private val UPDATE_INTERVAL = 10 * 1000 /* 10 secs */.toLong()
    private val FASTEST_INTERVAL: Long = 25000 /* 25 sec */
    val REQUEST_CHECK_SETTINGS: Int = 111
    private var _binding: FragmentAvaloirSearchBinding? = null
    private val binding get() = _binding!!
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var listener: AvaloirListAdapter.AvaloirListAdapterListener? = null
    var currentLocation: Location? = null

    lateinit var adapter: AvaloirListAdapter
    lateinit var locationRequest: LocationRequest
    var requestingLocationUpdates = false
    private lateinit var locationCallback: LocationCallback

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupButtons()
        initRecycler()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        showEnableLocationSetting()

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
                currentLocation = location
            }
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) locationUpdateReady()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                locationUpdateReady()
            } else if (resultCode == RESULT_CANCELED) {
                showEnableLocationDialog()
            }
        }
    }

    private fun setupButtons() {

        binding.btnAddAvaloir.setOnClickListener {
            currentLocation.let {
                avaloirModel.registerCoordinates(it!!.latitude, it.longitude)
            }

            findNavController().navigate(R.id.action_searchFragment_to_mapFragment)
        }

        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.btnSearch -> {
                    makeSearch()
                    true
                }
                R.id.bottomAppBar -> {
                    Toast.makeText(requireContext(), "Clicked navigation item", Toast.LENGTH_SHORT)
                        .show()
                    //findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
                    true
                }
                else -> false
            }
        }
        binding.bottomAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateUi() {
        currentLocation.let { location ->
            location.apply {
                binding.locationTextView.text = requireContext().getString(
                    R.string.avaloir_location_user,
                    location?.latitude.toString(),
                    location?.longitude.toString()
                )
                binding.btnAddAvaloir.visibility = View.VISIBLE
            }
        }
    }

    private fun makeSearch() {
        showProgressBar()
        currentLocation.let { location ->
            location.apply {
                avaloirModel.search(this!!.latitude, this.longitude, "25m")
                avaloirModel.resultSearch.observe(
                    viewLifecycleOwner,
                    Observer { searchResponse ->
                        val avaloirs = searchResponse.avaloirs
                        val count = avaloirs.size
                        avaloirs.let { adapter.setAvaloirs(avaloirs) }
                        binding.resultSearchTextView.text =
                            resources.getQuantityString(R.plurals.count_avaloir_found, count, count)
                        hideProgressBar()
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

    private fun showEnableLocationSetting() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateReady()
        }
        task.addOnFailureListener { exception ->

            if (exception is ResolvableApiException) {
                // Location settings are not satisfied
                try {
                    this.startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_CHECK_SETTINGS,
                        null, 0, 0, 0, null
                    )

                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        startActivity(intent)
    }

    private fun showEnableLocationDialog() {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("La localistation est obligatoire")
            .setMessage("Sans la localisation la recherche d'avaloirs n'est pas possible")
            .setPositiveButton("OK") { dialog, id ->
                openSettings()
            }
        dialog.show()
    }

    private fun showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE)
    }

    private fun hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE)
    }

    private fun locationUpdateReady() {
        requestingLocationUpdates = true
        startLocationUpdates()
    }
}