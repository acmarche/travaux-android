package be.marche.apptravaux

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ZEZE", "List activity create")
        setContent {
            AppTravaux6Theme {
                Surface(color = MaterialTheme.colors.background) {
                    Text(text = "Coucou")
                }
            }
        }
    }
}