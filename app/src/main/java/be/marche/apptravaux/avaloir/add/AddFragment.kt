package be.marche.apptravaux.avaloir.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirAddBinding
import be.marche.apptravaux.geofence.GeofenceManager
import be.marche.apptravaux.location.LocationViewModel
import be.marche.apptravaux.permission.PermissionUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class AddFragment : Fragment() {

    private var _binding: FragmentAvaloirAddBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel: LocationViewModel by sharedViewModel()
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    lateinit var permissionUtil: PermissionUtil
    lateinit var geofenceManager: GeofenceManager
    private lateinit var avaloirNew: Avaloir
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getLocation()

        geofenceManager = GeofenceManager(requireActivity().applicationContext)

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }

        latitude = 50.2360
        longitude = 5.3619
        Timber.w("zeze ma location $latitude $longitude")

        geofenceManager.createGeoFence(latitude!!, longitude!!, 1000.0f, "mageofence")

        binding.btnValider.setOnClickListener {
            if (longitude == null && longitude == null) {
                Toast.makeText(
                    context,
                    "Coordonnées vident",
                    Toast.LENGTH_LONG
                ).show()
                geofenceManager.removeAllGeofences()
            } else {

               // geofenceManager.geofencePendingIntent
                //add(latitude!!, longitude!!)

                Toast.makeText(
                    context,
                    "Avaloir ajouté",
                    Toast.LENGTH_LONG
                ).show()
                //  findNavController().navigate(R.id.action_addFragment_to_homeFragment)
            }
        }
    }

    private fun getLocation() {
        locationViewModel.getLocationData()
            .observe(viewLifecycleOwner, Observer { locationData ->
                binding.latitude.text = locationData.location?.latitude.toString()
                binding.longitude.text = locationData.location?.longitude.toString()
                latitude = locationData.location?.latitude
                longitude = locationData.location?.longitude
            })
    }

    private fun add(latitude: Double, longitude: Double) {
        avaloirNew = Avaloir(
            null,
            latitude,
            longitude
        )
        avaloirModel.insertAvaloir(avaloirNew)
    }

    /*  fun essai() {
          locationData.location?.let {
              if (firstLocation) {
                  firstLocation = false
                  Timber.w("zeze first lat " + locationData.location.latitude + " long " + (locationData.location.longitude))
              }

              binding.btnAdd.text =
                  (locationData.location.longitude + (locationData.location.latitude)).toString()
              Timber.w("zeze location lat " + (avaloirNew.latitude) + " long " + avaloirNew.longitude)
          }
      }*/
}