package be.marche.apptravaux.avaloir

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.avaloir.show.ShowFragment
import be.marche.apptravaux.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class RedirectActivity : AppCompatActivity() {

    private val avaloirModel: AvaloirViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val avaloirId = intent.getStringExtra("id")
        if (avaloirId != null && avaloirId.isNotEmpty()) {
            avaloirModel.getAvaloirById(avaloirId.toInt())
            intent = Intent(this, ShowFragment::class.java)
            startActivity(intent)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_addFragment)
        }

    }
}