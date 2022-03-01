package be.marche.apptravaux

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.AvaloirViewModel
import be.marche.apptravaux.viewModel.CardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListActivity : ComponentActivity() {

    private val avaloirViewModel: AvaloirViewModel by viewModels()
    private val cardViewModel: CardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appli = application as TravauxApplication
        appli.workerFactory

        Log.d("ZEZE", "List activity create")
        setContent {
            AppTravaux6Theme {

                cardViewModel.findByIdT(50)

                val avaloir: Avaloir? = cardViewModel.avaloir.collectAsState().value
                Log.d("ZEZE", "avaloir : ${avaloir}")

                Screen(avaloir)

            }
        }
    }
}

@Composable
fun Screen(avaloir: Avaloir?) {
    Log.d("ZEZE", "avaloir in screen : ${avaloir}")

    // State has only on Screen
    var checked by remember { mutableStateOf(false) }
    Row {
        MySwitch(initialChecked = checked, onCheckChanged = { checked = it })
        Text(
            text = if (checked) "on" else "false",
            Modifier.clickable {
                checked = !checked
            }
        )
    }
}

@Composable
fun MySwitch(initialChecked: Boolean, onCheckChanged: (Boolean) -> Unit) {
    // MutableState
    //  var checked2 = remember { mutableStateOf(checked) }
    // var checked by remember { mutableStateOf(initialEnabled) }

    Log.d("ZEZE", "switch $initialChecked")
    // ****↓Add****
    println("MySwitch(): $initialChecked")

    // ****↑Add****
    Column {
        /*    Switch(
                checked = checked2.value,
                onCheckedChange = {
                    checked2.value = it
                }
            )
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                }
            )*/
        Switch(
            checked = initialChecked,
            onCheckedChange = {
                onCheckChanged(it)
            }
        )
    }
}