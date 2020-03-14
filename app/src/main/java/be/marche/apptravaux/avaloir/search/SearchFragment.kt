package be.marche.apptravaux.avaloir.search

import android.location.Location
import android.os.Bundle
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
import be.marche.apptravaux.location.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class SearchFragment : Fragment(), AvaloirListAdapter.AvaloirListAdapterListener {

    private var _binding: FragmentAvaloirSearchBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel: LocationViewModel by sharedViewModel()
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var listener: AvaloirListAdapter.AvaloirListAdapterListener? = null

    private lateinit var avaloirNew: Avaloir
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var liveSearchResult: MutableLiveData<SearchResponse>

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
            add()
            findNavController().navigate(R.id.action_searchFragment_to_addFragment)
        }

        //map renvoie une valeur
        //switch map renvoie un live

        listener = this

        val recyclerView = binding.recyclerViewAvaloirs
        val adapter = context?.let { AvaloirListAdapter(listener) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location ->
                avaloirModel.search(location.latitude, location.longitude, "500km")
                avaloirModel.resultSearch.observe(viewLifecycleOwner, Observer { searchResponse ->
                    Timber.w("zeze live search " + searchResponse)
                    this.latitude = location.latitude
                    this.longitude = location.longitude
                    val avaloirs = searchResponse.avaloirs
                    avaloirs?.let { adapter?.setAvaloirs(avaloirs) }
                })
            }
    }

    override fun onAvaloirSelected(avaloir: Avaloir) {
        avaloirModel.setAvaloir(avaloir)
        findNavController().navigate(R.id.action_searchFragment_to_showFragment)
    }

    private fun add() {
        avaloirNew = Avaloir(
            null,
            222,
            this.latitude,
            this.longitude
        )
        avaloirModel.insertAvaloir(avaloirNew)
        avaloirModel.setAvaloir(avaloirNew)
    }

}