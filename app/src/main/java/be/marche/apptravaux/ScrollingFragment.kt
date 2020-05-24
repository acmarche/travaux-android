package be.marche.apptravaux

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import be.marche.apptravaux.databinding.FragmentScrollingBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_scrolling.*

class ScrollingFragment : Fragment() {
    private var _binding: FragmentScrollingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScrollingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

      //  (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

     /*   fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
    }
}