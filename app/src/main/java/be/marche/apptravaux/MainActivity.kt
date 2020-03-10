package be.marche.apptravaux

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
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

        //bug https://issuetracker.google.com/issues/142847973
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //val navController = findNavController(R.id.nav_host_fragment)
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

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

    /**
     * Pour la bar d'action
     * Quand on clic sur la fleche en haut a gauche, navigation gere le back
     */
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }
}
