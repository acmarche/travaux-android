package be.marche.apptravaux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Deprecated(message = "sert a rien")
class ListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appli = application as TravauxApplication
        appli.workerFactory

        setContent {
            AppTravaux6Theme {
                Screen()
            }
        }
    }
}

@Composable
fun Screen() {
    var checked by remember { mutableStateOf(false) }
    Row {
        Text(
            text = if (checked) "on" else "false",
            Modifier.clickable {
                checked = !checked
            }
        )
    }
}
