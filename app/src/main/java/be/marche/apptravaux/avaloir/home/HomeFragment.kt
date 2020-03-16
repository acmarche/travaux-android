package be.marche.apptravaux.avaloir.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.api.ConnectivityLiveData
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirHomeBinding
import be.marche.apptravaux.geofence.GeofenceManager
import be.marche.apptravaux.permission.PermissionUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {

    private val LOCATION_REQUEST_CODE = 1
    lateinit var permissionUtil: PermissionUtil
    private val avaloirModel: AvaloirViewModel by viewModel()
    private var _binding: FragmentAvaloirHomeBinding? = null
    private val binding get() = _binding!!

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

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.w("zeze no granted")
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                Timber.w("zeze show wy")
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                Timber.w("zeze no explanation")
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
Timber.w("zeze callback")
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Timber.w("zeze granted")
            // Permission has already been granted
        }

        // setupPermissions()

        refreshDataBase()

        binding.goBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)
        }
        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun refreshDataBase() {
        activity?.application?.let {
            ConnectivityLiveData(it).observe(viewLifecycleOwner, Observer { connected ->
                when (connected) {
                    true -> {
                        binding.errorTextView.text = getString(R.string.message_ok_connectivity)
                        binding.errorTextView.visibility = View.INVISIBLE
                        binding.btnSearch.isEnabled = true
                        //          syncContent()
                    }
                    false -> {
                        binding.errorTextView.visibility = View.VISIBLE
                        binding.btnSearch.isEnabled = false
                        binding.errorTextView.text = getString(R.string.message_no_connectivity)
                    }
                }
            })
        }
    }

    private fun syncContent() {
        avaloirModel.getAllAvaloirsFromServer().observe(viewLifecycleOwner, Observer { avaloirs ->
            Timber.w("zeze sync all avaloirs size: " + avaloirs.size)
            avaloirModel.insertAvaloirs(avaloirs)
        })
        avaloirModel.getDatesFromServer().observe(viewLifecycleOwner, Observer { dates ->
            Timber.w("zeze sync all dates size: " + dates.size)
            avaloirModel.insertDates(dates)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Timber.w("zeze grant " + grantResults + " coce " + requestCode)
                    Timber.w("zeze Permission has been denied by user")
                } else {
                    Timber.w("zeze Permission has been granted by user")
                }
            }
        }
    }

    private fun setupPermissions() {
        val message =
            "La localisation est requise pour gérer les avaloirs."
        permissionUtil.requestPermissionsWithExplanation(
            this,
            message,
            "Localiser precis",
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}