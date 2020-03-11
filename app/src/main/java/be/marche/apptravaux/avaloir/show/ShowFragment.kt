package be.marche.apptravaux.avaloir.show

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirShowBinding
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        avaloirModel.avaloir.observe(viewLifecycleOwner, Observer { avaloir ->
            this.avaloir = avaloir
            updateUi(avaloir)
        })

        binding.btnNettoye.setOnClickListener {

        }

        binding.btnNettoye.setOnClickListener {

        }

    }

    fun updateUi(avaloir: Avaloir) {

        binding.avaloirTextView.text =
            avaloir.latitude.toString() + "," + avaloir.latitude.toString()
        if (avaloir.imageUrl != null && avaloir.imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(avaloir.imageUrl)
                .placeholder(R.drawable.ic_photo_library)
                .into(binding.avalorImageView)
        }
    }
}