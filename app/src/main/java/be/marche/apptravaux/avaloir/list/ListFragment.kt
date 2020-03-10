package be.marche.apptravaux.avaloir.list

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirListBinding
import be.marche.apptravaux.location.LocationData
import be.marche.apptravaux.location.LocationViewModel
import be.marche.apptravaux.permission.PermissionUtil
import com.google.android.gms.common.api.ResolvableApiException
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val REQUEST_CHECK_SETTINGS = 1
private const val REQUEST_PERMISSION_START_UPDATE_LOCATION = 2

class ListFragment : Fragment() {

    private var _binding: FragmentAvaloirListBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel: LocationViewModel by sharedViewModel()
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    lateinit var permissionUtil: PermissionUtil
    private var firstLocation = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        permissionUtil = PermissionUtil(requireContext())

        val recyclerView = binding.recyclerViewAvaloirs
        val adapter = context?.let { RecyclerViewAdapter(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        avaloirModel.getAll().observe(viewLifecycleOwner, Observer { avaloirs ->
            avaloirs?.let { adapter?.setAvaloirs(avaloirs) }
        })

      //  startLocationUpdate()
    }

    private fun startLocationUpdate() {
        locationViewModel.getLocationData()
            .observe(viewLifecycleOwner, Observer { handleLocationData(it) })
    }

    private fun handleLocationData(locationData: LocationData) {
        if (handleLocationException(locationData.exception))
            return
    }

    private fun handleLocationException(exception: Exception?): Boolean {
        exception ?: return false

        when (exception) {
            is SecurityException -> checkLocationPermission(
                REQUEST_PERMISSION_START_UPDATE_LOCATION
            )
            is ResolvableApiException -> exception.startResolutionForResult(
                activity,
                REQUEST_CHECK_SETTINGS
            )
        }
        return true
    }

    private fun checkLocationPermission(requestCode: Int): Boolean {

        if (!permissionUtil.checkSelfPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            permissionUtil.requestPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, requestCode)
            return false
        }
        return true
    }
}