package be.marche.apptravaux.avaloir.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirHomeBinding
import be.marche.apptravaux.geofence.GeofenceManager
import be.marche.apptravaux.permission.PermissionUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class HomeFragment : Fragment() {

    private val RECORD_REQUEST_CODE = 1
    lateinit var permissionUtil: PermissionUtil
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private var _binding: FragmentAvaloirHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    val geofenceManager: GeofenceManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        permissionUtil = PermissionUtil(requireContext())
        setupPermissions2()
        avaloirModel.insertAvaloir(Avaloir(null, 50.2360,5.3619))

        avaloirModel.getAll().observe(viewLifecycleOwner, Observer { avaloirs ->
            for (avaloir in avaloirs) {
                Timber.w("zeze add fence " + avaloir.id)
                geofenceManager.addGeofenceToList(
                    avaloir.latitude, avaloir.longitude,
                    avaloir.id.toString()
                )
            }
        })

        binding.goBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)
        }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionUtil.RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Timber.w("zeze Permission has been denied by user")
                } else {
                    Timber.w("zeze Permission has been granted by user")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupPermissions() {

        if (permissionUtil.checkSelfPermissions(Manifest.permission.ACCESS_FINE_LOCATION) == false) {
            Timber.w("zeze ko")
            permissionUtil.requestPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setupPermissions2() {
        val message =
            "La localisation est requise pour gérer les avaloirs."
        permissionUtil.requestPermissionWithExplanation(
            this,
            message,
            "",
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

}