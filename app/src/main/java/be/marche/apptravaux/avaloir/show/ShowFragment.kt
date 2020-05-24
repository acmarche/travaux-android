package be.marche.apptravaux.avaloir.show

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.Commentaire
import be.marche.apptravaux.avaloir.entity.DateNettoyage
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.ContentScrollingBinding
import be.marche.apptravaux.databinding.FragmentAvaloirShowBinding
import coil.api.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ShowFragment : Fragment() {

    private var _binding: FragmentAvaloirShowBinding? = null
    private val binding get() = _binding!!
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var avaloir: Avaloir
    private lateinit var contentScroll: ContentScrollingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirShowBinding.inflate(inflater, container, false)
        contentScroll = binding.contentScroll
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_showFragment_to_homeFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initUi()

        avaloirModel.avaloir.observe(viewLifecycleOwner, Observer { avaloir ->

            setupButtons(avaloir)
            updateUi(avaloir)
            this.avaloir = avaloir

            avaloirModel.getDatesByAvaloirId(avaloir.idReferent)
                .observe(viewLifecycleOwner, Observer { dates ->
                    updateUiDates(dates)
                })

            avaloirModel.getCommentairesByAvaloirId(avaloir.idReferent)
                .observe(viewLifecycleOwner, Observer { commentaires ->
                    updateUiCommentaires(commentaires)
                })
        })

    }

    private fun setupButtons(avaloir: Avaloir) {
        binding.btnAddClean.setOnClickListener {
            Timber.w("zeze add cleaning")
            updateClean(avaloir)
        }

        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.btnComment -> {
                    createDialogueBox()
                    true
                }
                else -> false
            }
        }
        binding.bottomAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_showFragment_to_homeFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateClean(avaloir: Avaloir) {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        avaloirModel.addCleaningDateAsync(avaloir, timeStamp)
    }

    private fun initUi() {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.loading)

        contentScroll.coordinatesTextView.text = getString(R.string.loading)

        contentScroll.avaloirImageView.load(R.drawable.ic_photo_library) {

        }
    }

    private fun updateUi(avaloir: Avaloir) {
        (activity as AppCompatActivity).supportActionBar?.title = getString(
            R.string.avaloit_title_show,
            avaloir.idReferent.toString()
        )

        contentScroll.coordinatesTextView.text = getString(
            R.string.avaloir_location_title,
            avaloir.latitude.toString(),
            avaloir.latitude.toString()
        )

        if (avaloir.imageUrl != null) {
            contentScroll.avaloirImageView.load(avaloir.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_photo_library)
            }
        }
    }

    private fun updateUiDates(dates: List<DateNettoyage>?) {
        val builder = StringBuilder()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        if (dates != null) {
            dates.forEach { date ->
                builder.append(format.format(date.date))
                builder.append(System.getProperty("line.separator"));
            }
            contentScroll.datesTextView.text = builder.toString()
        }
    }

    private fun updateUiCommentaires(commentaires: List<Commentaire>?) {
        val builder = StringBuilder()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        if (commentaires != null) {
            commentaires.forEach { commentaire ->
                builder.append(commentaire.content)
                builder.append(" ajouté le ")
                builder.append(format.format(commentaire.createdAt))
                builder.append(System.getProperty("line.separator"));
            }
            contentScroll.commentairesTextView.text = builder.toString()
        }
    }

    private fun createDialogueBox() {
        Timber.w("zeze create box")
        val customView = layoutInflater.inflate(R.layout.add_comment, null)
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Ajouter un commentaire")
            .setView(customView)
            .setPositiveButton("OK") { dialog, id ->
                val commentaire = customView.findViewById<TextView>(R.id.inputComment).text
                sendCommentaire(commentaire)
            }
            .setNegativeButton("Annuler") { dialog, id -> dialog.cancel() }
        dialog.show()
    }

    private fun sendCommentaire(commentaire: CharSequence?) {
        if (commentaire != null && commentaire.length > 0)
            avaloirModel.addCommentAsync(avaloir, commentaire)
    }
}