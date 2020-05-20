package be.marche.apptravaux.avaloir.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentMapBinding
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //   loadingProgressBar.hide()
        map.setOnMarkerDragListener(this)

        val lntLng =
            LatLng(avaloirModel.coordinates.latitude, avaloirModel.coordinates.longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lntLng, 15f))
        val options = MarkerOptions()
            .position(LatLng(avaloirModel.coordinates.latitude, avaloirModel.coordinates.longitude))
            // .title(avaloir.rue)
            //  .snippet(avaloir.localite)
            .draggable(true)

        marker = map.addMarker(options)
    }

    override fun onMarkerDragEnd(marker: Marker) {
        Timber.w("zeze drag end")
        avaloirModel.registerCoordinates(marker.position.latitude, marker.position.longitude)
        val lntLng =
            LatLng(marker.position.latitude, marker.position.longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lntLng, 15f))
    }

    override fun onMarkerDragStart(marker: Marker?) {

    }

    override fun onMarkerDrag(marker: Marker?) {

    }

}