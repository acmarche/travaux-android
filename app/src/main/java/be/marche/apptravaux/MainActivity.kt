package be.marche.apptravaux

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import be.marche.apptravaux.navigation.Navigation
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.AvaloirViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    object MyBitmap {
        var bitmap: Bitmap? = null
    }

    private val avaloirViewModel: AvaloirViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  syncContent()
        setContent {
            AppTravaux6Theme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Appli Travaux",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        Navigation(avaloirViewModel)
                    }
                }
            }
        }
    }

    private fun syncContent() {
        avaloirViewModel.getAllAvaloirsFromServer().observe(this) { avaloirs ->
            avaloirViewModel.insertAvaloirs(avaloirs)
        }
    }

    @Preview
    @Composable
    fun PreviewConversation() {
        AppTravaux6Theme {

        }
    }
}
