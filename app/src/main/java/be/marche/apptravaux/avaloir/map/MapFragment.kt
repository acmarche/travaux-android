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
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

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
                    Timber.w("zeze new loc" + location)
                    moveMap(location.latitude, location.longitude)
                    moveMarker(location.latitude, location.longitude)
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

    private fun moveMap(latitude: Double, longitude: Double, zoom: Float = 15f) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //   loadingProgressBar.hide()
        map.setOnMarkerDragListener(this)

        moveMap(avaloirModel.coordinates.latitude, avaloirModel.coordinates.longitude)
        moveMarker(avaloirModel.coordinates.latitude, avaloirModel.coordinates.longitude)
    }

    protected fun moveMarker(latitude: Double, longitude: Double) {
        val options = MarkerOptions()
            .position(LatLng(latitude, longitude))
            // .title(avaloir.rue)
            //  .snippet(avaloir.localite)
            .draggable(true)

        map.clear()
        marker = map.addMarker(options)
    }

    protected fun updateUi() {
        binding.coordinatesTextView.text = getString(
            R.string.avaloir_location_title,
            avaloirModel.coordinates.latitude.toString(),
            avaloirModel.coordinates.longitude.toString()
        )
    }

    override fun onMarkerDragEnd(marker: Marker) {
        Timber.w("zeze drag end")
        avaloirModel.registerCoordinates(marker.position.latitude, marker.position.longitude)
        moveMap(marker.position.latitude, marker.position.longitude)
        updateUi()
    }

    override fun onMarkerDragStart(marker: Marker?) {

    }

    override fun onMarkerDrag(marker: Marker?) {

    }

}