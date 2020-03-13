package be.marche.apptravaux.avaloir.add

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.SearchResponse
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirAddBinding
import be.marche.apptravaux.geofence.GeofenceManager
import be.marche.apptravaux.location.LocationViewModel
import be.marche.apptravaux.permission.PermissionUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class AddFragment : Fragment() {

    private var _binding: FragmentAvaloirAddBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel: LocationViewModel by sharedViewModel()
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var avaloirNew: Avaloir
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var liveSearchResult: MutableLiveData<SearchResponse>

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

        //map renvoie une valeur
        //switch map renvoie un live

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location ->
                avaloirModel.search(location.latitude, location.longitude, "500km")
                avaloirModel.resultSearch.observe(viewLifecycleOwner, Observer { searchResponse ->
                    Timber.w("zeze live search " + searchResponse)

                    for (avaloir in searchResponse.avaloirs) {

                    }

                })
            }

/*        liveSearchResult.observe(viewLifecycleOwner, Observer {

        })*/

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }

        binding.btnValider.setOnClickListener {
            if (this.longitude == null && this.longitude == null) {
                Toast.makeText(
                    context,
                    "Coordonnées vident",
                    Toast.LENGTH_LONG
                ).show()

            } else {

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
                this.latitude = locationData.location?.latitude!!
                this.longitude = locationData.location?.longitude!!
            })
    }

    private fun add(latitude: Double, longitude: Double) {
        avaloirNew = Avaloir(
            null,
            22,
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
    /*fun del() {
        materialButtonDeleteWord.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.delete_dialog, null)
            dialogBuilder.setView(dialogView)
            dialogBuilder.setPositiveButton("Submit") { dialogInterface, i ->
                activityViewModel.deleteWord(dialogView.etView.text.toString())
            }
            val b = dialogBuilder.create()
            b.show()

        }
    }*/
}