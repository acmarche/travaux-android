package be.marche.apptravaux.avaloir.show

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.DateNettoyage
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirShowBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class ShowFragment : Fragment() {

    private var _binding: FragmentAvaloirShowBinding? = null
    private val binding get() = _binding!!
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var avaloir: Avaloir

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_showFragment_to_searchFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        avaloirModel.avaloir.observe(viewLifecycleOwner, Observer { avaloir ->

            updateUi(avaloir)

            avaloirModel.getDatesByAvaloirId(avaloir.idReferent)
                .observe(viewLifecycleOwner, Observer { dates ->
                    updateUiDates(dates)
                })

            binding.btnClean.setOnClickListener {
                updateClean(avaloir)
            }

            binding.btnComment.setOnClickListener {
                createDialogueBox()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateClean(avaloir: Avaloir) {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        avaloirModel.addCleaningDateAsync(avaloir, timeStamp)
    }

    private fun updateUi(avaloir: Avaloir) {
        binding.coordinatesTextView.text = getString(
            R.string.avaloir_location_title,
            avaloir.latitude.toString(),
            avaloir.latitude.toString()
        )

        if (avaloir.imageUrl != null) {
            Picasso.get()
                .load(avaloir.imageUrl)
                .placeholder(R.drawable.ic_photo_library)
                .into(binding.avalorImageView)
        }
    }

    private fun updateUiDates(dates: List<DateNettoyage>?) {
        val builder = StringBuilder()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        if (dates != null) {
            for (date in dates) {
                builder.append(format.format(date.date))
                builder.append(System.getProperty("line.separator"));
            }
            binding.datesTextView.text = builder.toString()
        }
    }

    private fun createDialogueBox() {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Ajout d'un commentaire")
            .setMessage("Pas encore implémenté :-P")
            .setPositiveButton(
                "OK"
            ) { dialog, id ->
                null
            }

        dialog.show()
    }
}