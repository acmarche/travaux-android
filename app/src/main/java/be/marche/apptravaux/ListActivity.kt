package be.marche.apptravaux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.navigation.Navigation
import be.marche.apptravaux.screens.avaloir.ItemAvaloir
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.AvaloirViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListActivity : ComponentActivity() {

    private val avaloirViewModel: AvaloirViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTravaux6Theme {
                //Conversation(SampleData.conversationSample)
                //LoadNews()
                Navigation(avaloirViewModel)
            }
        }
    }
}

@Composable
fun WeatherLoadedScreen(avaloirs: List<Avaloir>) {
    LazyColumn {
        items(avaloirs) { avaloir ->
         //   ItemAvaloir(avaloir)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    AppTravaux6Theme {
        //Conversation(SampleData.conversationSample)
    }
}
