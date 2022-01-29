package be.marche.apptravaux

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.WordViewModel
import be.marche.apptravaux.viewModel.WordViewModelFactory

class WordActivity : ComponentActivity() {
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as TravauxApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        wordViewModel.allWords.observe( this) { words ->
            // Update the cached copy of the words in the adapter.
            Log.e("ZEZE", words.toString())
        }

        setContent {
            AppTravaux6Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                }
            }
        }
    }
}