package be.marche.apptravaux.avaloir.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentMapBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*
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
            .replace(R.id.content, mapFragment)
            .commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        loadingProgressBar.hide()
        map.setOnMarkerDragListener(this)

        avaloirModel.avaloir.observe(viewLifecycleOwner, Observer { avaloir ->
            Timber.w("zeze avaloir " + avaloir)
            val lntLng =
                LatLng(avaloir.latitude, avaloir.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lntLng, 15f))
            val options = MarkerOptions()
                .position(LatLng(avaloir.latitude, avaloir.longitude))
                .title(avaloir.rue)
                .snippet(avaloir.localite)
                .draggable(true)

            map.addMarker(options)
        })

    }

    override fun onMarkerDragEnd(marker: Marker?) {
        TODO("Not yet implemented")
    }

    override fun onMarkerDragStart(marker: Marker?) {
        TODO("Not yet implemented")
    }

    override fun onMarkerDrag(marker: Marker?) {
        TODO("Not yet implemented")
    }

}