package be.marche.apptravaux

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import be.marche.apptravaux.avaloir.search.SearchFragment
import be.marche.apptravaux.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var searchFragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //bug https://issuetracker.google.com/issues/142847973
        //val navController = findNavController(R.id.nav_host_fragment)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        searchFragment = SearchFragment.newInstance()
        //  getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, searchFragment).commit()

    }

    /**
     * Pour la bar d'action
     * Quand on clic sur la fleche en haut a gauche, navigation gere le back
     */
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

     fun onActivityResult22(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.w("zeze activity result ")
        if (requestCode == searchFragment.REQUEST_CHECK_SETTINGS) {
       /*     searchFragment.startIntentSenderForResult(
                status.getResolution().getIntentSender(),
                REQUEST_CHECK_SETTINGS,
                null,
                0,
                0,
                0,
                null
            );

            searchFragment.onActivityResult(requestCode, resultCode, data);*/
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
