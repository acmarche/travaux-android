package be.marche.apptravaux

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import be.marche.apptravaux.R
import be.marche.apptravaux.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    // private val ficheViewModel: FicheViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*   ficheViewModel.getFichesByCategory(512).observe(this, Observer { fiches ->
               for (fiche in fiches) {
                   if (!(fiche.latitude!!.isNotEmpty() || fiche.longitude!!.isNotEmpty())) {
                       Timber.w("zeze" + fiche.societe)
                       Timber.w("zeze" + fiche.latitude)
                       Timber.w("zeze" + fiche.longitude)
                   }
               }

           })*/

    }
}
