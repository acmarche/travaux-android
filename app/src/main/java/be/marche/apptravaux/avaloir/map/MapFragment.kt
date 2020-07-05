package be.marche.apptravaux.avaloir.map

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    lateinit var map: GoogleMap
    lateinit var marker: Marker
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupBtn()
        updateUi()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    moveMap(location.latitude, location.longitude)
                    addMarker(location.latitude, location.longitude)
                }
                //  fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }

        val mapOptions = GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NORMAL)
            .zoomControlsEnabled(true)
            .zoomGesturesEnabled(true)

        val mapFragment = SupportMapFragment.newInstance(mapOptions)

        mapFragment.getMapAsync(this)

        getParentFragmentManager().beginTransaction()
            .replace(R.id.mapView, mapFragment)
            .commit()
    }

    protected fun setupBtn() {
        binding.btnValidLocation.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_addFragment)
        }
        binding.btnRefreshLocation.setOnClickListener {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationRequest = LocationRequest.create().apply {
            interval = 10 * 1000
            fastestInterval = 25000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun moveMap(latitude: Double, longitude: Double, zoom: Float = 18f) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
    }

    private fun addMarker(latitude: Double, longitude: Double) {
        val options = MarkerOptions()
            .position(LatLng(latitude, longitude))
            // .title(avaloir.rue)
            //  .snippet(avaloir.localite)
        map.clear()
        marker = map.addMarker(options)
    }

    private fun moveMarker(latitude: Double, longitude: Double) {
        val latLng = LatLng(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
        marker.position = latLng
    }

    private fun updateUi() {
        binding.coordinatesTextView.text = getString(
            R.string.avaloir_location_title,
            avaloirModel.coordinates.latitude.toString(),
            avaloirModel.coordinates.longitude.toString()
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //   loadingProgressBar.hide()

        moveMap(avaloirModel.coordinates.latitude, avaloirModel.coordinates.longitude)
        addMarker(avaloirModel.coordinates.latitude, avaloirModel.coordinates.longitude)

        map.setOnCameraMoveListener {
            moveMarker(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
        }

        map.setOnCameraIdleListener {
            val latLng =
                LatLng(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
            moveMarker(latLng.latitude, latLng.longitude)
            avaloirModel.registerCoordinates(latLng.latitude, latLng.longitude)
            updateUi()
        }
    }
}